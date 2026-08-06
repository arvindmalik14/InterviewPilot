package com.malik.InterviewPilot.aiqa.service;

import com.malik.InterviewPilot.aiqa.dto.category.AiCategoryRequest;
import com.malik.InterviewPilot.aiqa.dto.category.AiCategoryResponse;
import com.malik.InterviewPilot.aiqa.entity.AiCategory;
import com.malik.InterviewPilot.aiqa.entity.AiContentStatus;
import com.malik.InterviewPilot.aiqa.repository.AiCategoryRepository;
import com.malik.InterviewPilot.aiqa.repository.AiQuestionCategoryRepository;
import com.malik.InterviewPilot.exception.DuplicateResourceException;
import com.malik.InterviewPilot.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AiCategoryServiceTest {

    private AiCategoryRepository categoryRepository;
    private AiQuestionCategoryRepository questionCategoryRepository;
    private AiCategoryService categoryService;

    @BeforeEach
    void setUp() {
        categoryRepository = mock(AiCategoryRepository.class);
        questionCategoryRepository = mock(AiQuestionCategoryRepository.class);
        categoryService = new AiCategoryService(categoryRepository, questionCategoryRepository);
    }

    @Test
    void createCategory_savesWithActiveStatus_whenStatusNotProvided() {
        AiCategoryRequest request = new AiCategoryRequest("Kubernetes", "Container orchestration", null);
        when(categoryRepository.existsByNameIgnoreCase("Kubernetes")).thenReturn(false);
        when(categoryRepository.save(any())).thenAnswer(inv -> {
            AiCategory saved = inv.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        AiCategoryResponse response = categoryService.createCategory(request);

        assertEquals("Kubernetes", response.name());
        assertEquals(AiContentStatus.ACTIVE, response.status());
        verify(categoryRepository).save(argThat(c -> c.getStatus() == AiContentStatus.ACTIVE));
    }

    @Test
    void createCategory_rejectsDuplicateName() {
        AiCategoryRequest request = new AiCategoryRequest("Java", "desc", null);
        when(categoryRepository.existsByNameIgnoreCase("Java")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> categoryService.createCategory(request));
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void updateCategory_throwsResourceNotFound_whenCategoryMissing() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> categoryService.updateCategory(99L, new AiCategoryRequest("X", null, null)));
    }

    @Test
    void updateCategory_allowsKeepingTheSameName() {
        AiCategory existing = AiCategory.builder().id(1L).name("Docker").status(AiContentStatus.ACTIVE).build();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AiCategoryResponse response = categoryService.updateCategory(1L,
                new AiCategoryRequest("Docker", "Updated description", AiContentStatus.INACTIVE));

        assertEquals("Docker", response.name());
        assertEquals(AiContentStatus.INACTIVE, response.status());
        verify(categoryRepository, never()).existsByNameIgnoreCase(any());
    }

    @Test
    void deleteCategory_throwsResourceNotFound_whenCategoryMissing() {
        when(categoryRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategory(5L));
    }

    @Test
    void deleteCategory_removesQuestionLinks_beforeDeletingTheCategory() {
        // Regression test: deleting a category that still has ai_question_category rows previously
        // failed with a foreign-key constraint violation (500).
        AiCategory existing = AiCategory.builder().id(3L).name("Docker").build();
        when(categoryRepository.findById(3L)).thenReturn(Optional.of(existing));

        categoryService.deleteCategory(3L);

        verify(questionCategoryRepository).deleteByCategory_Id(3L);
        verify(categoryRepository).delete(existing);
    }
}
