package com.malik.InterviewPilot.aiqa.controller;

import com.malik.InterviewPilot.aiqa.dto.category.AiCategoryRequest;
import com.malik.InterviewPilot.aiqa.dto.category.AiCategoryResponse;
import com.malik.InterviewPilot.aiqa.dto.question.AiQuestionAdminResponse;
import com.malik.InterviewPilot.aiqa.dto.question.AiQuestionRequest;
import com.malik.InterviewPilot.aiqa.dto.question.AiQuestionSummaryResponse;
import com.malik.InterviewPilot.aiqa.entity.AiContentStatus;
import com.malik.InterviewPilot.aiqa.service.AiCategoryService;
import com.malik.InterviewPilot.aiqa.service.AiPlanQuestionService;
import com.malik.InterviewPilot.aiqa.service.AiQuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * All endpoints here fall under SecurityConfig's `/api/admin/**` -> hasRole("ADMIN") path rule,
 * so no per-method @PreAuthorize is needed (same convention as the existing AdminController).
 */
@RestController
@RequestMapping("/api/admin/ai")
@RequiredArgsConstructor
public class AiAdminController {

    private final AiCategoryService categoryService;
    private final AiQuestionService questionService;
    private final AiPlanQuestionService planQuestionService;

    // ---- Categories ----

    @GetMapping("/categories")
    public List<AiCategoryResponse> listCategories() {
        return categoryService.listAllCategories();
    }

    @PostMapping("/categories")
    public ResponseEntity<AiCategoryResponse> createCategory(@Valid @RequestBody AiCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(request));
    }

    @PutMapping("/categories/{id}")
    public AiCategoryResponse updateCategory(@PathVariable Long id, @Valid @RequestBody AiCategoryRequest request) {
        return categoryService.updateCategory(id, request);
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    // ---- Questions ----

    @GetMapping("/questions")
    public Page<AiQuestionAdminResponse> listQuestions(Pageable pageable) {
        return questionService.listForAdmin(pageable);
    }

    @PostMapping("/questions")
    public ResponseEntity<AiQuestionAdminResponse> createQuestion(@Valid @RequestBody AiQuestionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(questionService.createQuestion(request));
    }

    @PutMapping("/questions/{id}")
    public AiQuestionAdminResponse updateQuestion(@PathVariable Long id, @Valid @RequestBody AiQuestionRequest request) {
        return questionService.updateQuestion(id, request);
    }

    @DeleteMapping("/questions/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/questions/{id}/activate")
    public AiQuestionAdminResponse activateQuestion(@PathVariable Long id) {
        return questionService.setStatus(id, AiContentStatus.ACTIVE);
    }

    @PostMapping("/questions/{id}/deactivate")
    public AiQuestionAdminResponse deactivateQuestion(@PathVariable Long id) {
        return questionService.setStatus(id, AiContentStatus.INACTIVE);
    }

    // ---- Plan assignment ----

    @PostMapping("/plans/{planId}/questions/{questionId}")
    public ResponseEntity<Void> assignQuestionToPlan(@PathVariable Long planId, @PathVariable Long questionId) {
        planQuestionService.assignQuestionToPlan(planId, questionId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/plans/{planId}/questions/{questionId}")
    public ResponseEntity<Void> removeQuestionFromPlan(@PathVariable Long planId, @PathVariable Long questionId) {
        planQuestionService.removeQuestionFromPlan(planId, questionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/plans/{planId}/questions")
    public Page<AiQuestionSummaryResponse> listQuestionsForPlan(@PathVariable Long planId, Pageable pageable) {
        return planQuestionService.listQuestionsForPlan(planId, pageable);
    }
}
