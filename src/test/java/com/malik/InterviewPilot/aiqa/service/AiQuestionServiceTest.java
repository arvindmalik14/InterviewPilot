package com.malik.InterviewPilot.aiqa.service;

import com.malik.InterviewPilot.aiqa.dto.question.AiQuestionAdminResponse;
import com.malik.InterviewPilot.aiqa.dto.question.AiQuestionRequest;
import com.malik.InterviewPilot.aiqa.entity.AiCategory;
import com.malik.InterviewPilot.aiqa.entity.AiContentStatus;
import com.malik.InterviewPilot.aiqa.entity.AiQuestion;
import com.malik.InterviewPilot.aiqa.repository.AiPlanQuestionRepository;
import com.malik.InterviewPilot.aiqa.repository.AiQuestionBookmarkRepository;
import com.malik.InterviewPilot.aiqa.repository.AiQuestionCategoryRepository;
import com.malik.InterviewPilot.aiqa.repository.AiQuestionRepository;
import com.malik.InterviewPilot.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AiQuestionServiceTest {

    private AiQuestionRepository questionRepository;
    private AiQuestionCategoryRepository questionCategoryRepository;
    private AiCategoryService categoryService;
    private AiPlanQuestionRepository planQuestionRepository;
    private AiQuestionBookmarkRepository bookmarkRepository;
    private AiQuestionService questionService;

    @BeforeEach
    void setUp() {
        questionRepository = mock(AiQuestionRepository.class);
        questionCategoryRepository = mock(AiQuestionCategoryRepository.class);
        categoryService = mock(AiCategoryService.class);
        planQuestionRepository = mock(AiPlanQuestionRepository.class);
        bookmarkRepository = mock(AiQuestionBookmarkRepository.class);
        questionService = new AiQuestionService(
                questionRepository, questionCategoryRepository, categoryService, planQuestionRepository, bookmarkRepository);
    }

    @Test
    void createQuestion_savesQuestionAndLinksEachRequestedCategory() {
        AiQuestionRequest request = new AiQuestionRequest(
                "What is dependency injection?", "answer", "example", "EASY", null, List.of(1L, 2L));
        when(questionRepository.save(any())).thenAnswer(inv -> {
            AiQuestion q = inv.getArgument(0);
            q.setId(100L);
            return q;
        });
        when(categoryService.findCategoryOrThrow(1L)).thenReturn(AiCategory.builder().id(1L).name("Java").build());
        when(categoryService.findCategoryOrThrow(2L)).thenReturn(AiCategory.builder().id(2L).name("Spring Boot").build());
        when(questionCategoryRepository.findByQuestion_Id(100L)).thenReturn(List.of());

        AiQuestionAdminResponse response = questionService.createQuestion(request);

        assertEquals("What is dependency injection?", response.title());
        assertEquals(AiContentStatus.ACTIVE, response.status());
        verify(questionCategoryRepository, times(2)).save(any());
    }

    @Test
    void updateQuestion_resyncsCategories_removingOldLinksFirst() {
        AiQuestion existing = AiQuestion.builder().id(5L).title("Old").status(AiContentStatus.ACTIVE).build();
        when(questionRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(questionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(categoryService.findCategoryOrThrow(9L)).thenReturn(AiCategory.builder().id(9L).name("SQL").build());
        when(questionCategoryRepository.findByQuestion_Id(5L)).thenReturn(List.of());

        AiQuestionRequest request = new AiQuestionRequest("New title", "answer", null, "MEDIUM", AiContentStatus.INACTIVE, List.of(9L));
        AiQuestionAdminResponse response = questionService.updateQuestion(5L, request);

        assertEquals("New title", response.title());
        assertEquals(AiContentStatus.INACTIVE, response.status());
        verify(questionCategoryRepository).deleteByQuestion_Id(5L);
    }

    @Test
    void findQuestionOrThrow_throwsResourceNotFound_whenMissing() {
        when(questionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> questionService.findQuestionOrThrow(1L));
    }

    @Test
    void deleteQuestion_removesCategoryLinksPlanAssignmentsAndBookmarks_beforeDeletingTheQuestion() {
        // Regression test: deleting a question that still has ai_plan_question/ai_question_category/
        // ai_question_bookmark rows previously failed with a foreign-key constraint violation (500).
        AiQuestion existing = AiQuestion.builder().id(7L).build();
        when(questionRepository.findById(7L)).thenReturn(Optional.of(existing));

        questionService.deleteQuestion(7L);

        verify(questionCategoryRepository).deleteByQuestion_Id(7L);
        verify(planQuestionRepository).deleteByQuestion_Id(7L);
        verify(bookmarkRepository).deleteByQuestion_Id(7L);
        verify(questionRepository).delete(existing);
    }
}
