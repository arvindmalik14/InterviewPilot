package com.malik.InterviewPilot.aiqa.service;

import com.malik.InterviewPilot.aiqa.dto.question.AiQuestionAdminResponse;
import com.malik.InterviewPilot.aiqa.dto.question.AiQuestionRequest;
import com.malik.InterviewPilot.aiqa.dto.question.CategorySummary;
import com.malik.InterviewPilot.aiqa.entity.AiCategory;
import com.malik.InterviewPilot.aiqa.entity.AiContentStatus;
import com.malik.InterviewPilot.aiqa.entity.AiQuestion;
import com.malik.InterviewPilot.aiqa.entity.AiQuestionCategory;
import com.malik.InterviewPilot.aiqa.repository.AiPlanQuestionRepository;
import com.malik.InterviewPilot.aiqa.repository.AiQuestionCategoryRepository;
import com.malik.InterviewPilot.aiqa.repository.AiQuestionBookmarkRepository;
import com.malik.InterviewPilot.aiqa.repository.AiQuestionRepository;
import com.malik.InterviewPilot.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class AiQuestionService {

    private final AiQuestionRepository questionRepository;
    private final AiQuestionCategoryRepository questionCategoryRepository;
    private final AiCategoryService categoryService;
    private final AiPlanQuestionRepository planQuestionRepository;
    private final AiQuestionBookmarkRepository bookmarkRepository;

    @Transactional(readOnly = true)
    public Page<AiQuestion> searchWithin(List<Long> accessibleIds, Long categoryId, String difficulty, String search, Pageable pageable) {
        return questionRepository.search(accessibleIds, AiContentStatus.ACTIVE, categoryId, difficulty, blankToNull(search), pageable);
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }

    @Transactional(readOnly = true)
    public Page<AiQuestionAdminResponse> listForAdmin(Pageable pageable) {
        Page<AiQuestion> page = questionRepository.findAll(pageable);
        Map<Long, List<CategorySummary>> categoriesByQuestion = categoriesForQuestions(page.getContent().stream().map(AiQuestion::getId).toList());
        return page.map(q -> AiQuestionAdminResponse.from(q, categoriesByQuestion.getOrDefault(q.getId(), List.of())));
    }

    public AiQuestionAdminResponse createQuestion(AiQuestionRequest request) {
        AiQuestion question = AiQuestion.builder()
                .title(request.title())
                .detailedAnswer(request.detailedAnswer())
                .realWorldExample(request.realWorldExample())
                .difficultyLevel(request.difficultyLevel())
                .status(request.status() != null ? request.status() : AiContentStatus.ACTIVE)
                .build();
        question = questionRepository.save(question);
        syncCategories(question, request.categoryIds());
        return AiQuestionAdminResponse.from(question, categoriesFor(question.getId()));
    }

    public AiQuestionAdminResponse updateQuestion(Long questionId, AiQuestionRequest request) {
        AiQuestion question = findQuestionOrThrow(questionId);
        question.setTitle(request.title());
        question.setDetailedAnswer(request.detailedAnswer());
        question.setRealWorldExample(request.realWorldExample());
        question.setDifficultyLevel(request.difficultyLevel());
        if (request.status() != null) {
            question.setStatus(request.status());
        }
        question = questionRepository.save(question);
        syncCategories(question, request.categoryIds());
        return AiQuestionAdminResponse.from(question, categoriesFor(question.getId()));
    }

    public void deleteQuestion(Long questionId) {
        AiQuestion question = findQuestionOrThrow(questionId);
        questionCategoryRepository.deleteByQuestion_Id(questionId);
        planQuestionRepository.deleteByQuestion_Id(questionId);
        bookmarkRepository.deleteByQuestion_Id(questionId);
        questionRepository.delete(question);
    }

    public AiQuestionAdminResponse setStatus(Long questionId, AiContentStatus status) {
        AiQuestion question = findQuestionOrThrow(questionId);
        question.setStatus(status);
        question = questionRepository.save(question);
        return AiQuestionAdminResponse.from(question, categoriesFor(question.getId()));
    }

    @Transactional(readOnly = true)
    public AiQuestion findQuestionOrThrow(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("AI question not found: " + questionId));
    }

    @Transactional(readOnly = true)
    public List<CategorySummary> categoriesFor(Long questionId) {
        return questionCategoryRepository.findByQuestion_Id(questionId).stream()
                .map(AiQuestionCategory::getCategory)
                .sorted(Comparator.comparing(AiCategory::getName))
                .map(CategorySummary::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public Map<Long, List<CategorySummary>> categoriesForQuestions(List<Long> questionIds) {
        if (questionIds.isEmpty()) {
            return Map.of();
        }
        return questionCategoryRepository.findByQuestion_IdIn(questionIds).stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        qc -> qc.getQuestion().getId(),
                        java.util.stream.Collectors.mapping(qc -> CategorySummary.from(qc.getCategory()), java.util.stream.Collectors.toList())));
    }

    private void syncCategories(AiQuestion question, List<Long> categoryIds) {
        questionCategoryRepository.deleteByQuestion_Id(question.getId());
        for (Long categoryId : categoryIds) {
            AiCategory category = categoryService.findCategoryOrThrow(categoryId);
            questionCategoryRepository.save(AiQuestionCategory.builder().question(question).category(category).build());
        }
    }
}
