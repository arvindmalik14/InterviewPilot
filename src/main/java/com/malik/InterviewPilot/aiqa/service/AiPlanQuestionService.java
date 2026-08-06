package com.malik.InterviewPilot.aiqa.service;

import com.malik.InterviewPilot.aiqa.dto.question.AiQuestionDetailResponse;
import com.malik.InterviewPilot.aiqa.dto.question.AiQuestionSummaryResponse;
import com.malik.InterviewPilot.aiqa.entity.AiPlanQuestion;
import com.malik.InterviewPilot.aiqa.entity.AiQuestion;
import com.malik.InterviewPilot.aiqa.repository.AiPlanQuestionRepository;
import com.malik.InterviewPilot.aiqa.repository.AiQuestionBookmarkRepository;
import com.malik.InterviewPilot.exception.DuplicateResourceException;
import com.malik.InterviewPilot.exception.ResourceNotFoundException;
import com.malik.InterviewPilot.razorpay.entity.SubscriptionStatus;
import com.malik.InterviewPilot.razorpay.entity.UserSubscription;
import com.malik.InterviewPilot.razorpay.repository.SubscriptionRepository;
import com.malik.InterviewPilot.razorpay.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Mirrors razorpay.service.PlanQuestionService's quota algorithm (junction-row membership,
 * capped by the plan's questionLimit, ordered by question id) but for the AI Q&A catalog's own
 * ai_plan_question junction — the two catalogs (MCQ vs AI Q&A) are deliberately kept as separate
 * accessible-question pools per the module's own DB design.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AiPlanQuestionService {

    private final AiPlanQuestionRepository planQuestionRepository;
    private final AiQuestionService questionService;
    private final AiQuestionBookmarkRepository bookmarkRepository;
    private final PlanService planService;
    private final SubscriptionRepository subscriptionRepository;

    public void assignQuestionToPlan(Long planId, Long questionId) {
        var plan = planService.findPlanOrThrow(planId);
        AiQuestion question = questionService.findQuestionOrThrow(questionId);
        if (planQuestionRepository.existsByPlan_PlanIdAndQuestion_Id(planId, questionId)) {
            throw new DuplicateResourceException("Question " + questionId + " is already assigned to plan " + planId);
        }
        planQuestionRepository.save(AiPlanQuestion.builder().plan(plan).question(question).build());
    }

    public void removeQuestionFromPlan(Long planId, Long questionId) {
        AiPlanQuestion link = planQuestionRepository.findByPlan_PlanIdAndQuestion_Id(planId, questionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Question " + questionId + " is not assigned to plan " + planId));
        planQuestionRepository.delete(link);
    }

    @Transactional(readOnly = true)
    public Page<AiQuestionSummaryResponse> listQuestionsForPlan(Long planId, Pageable pageable) {
        planService.findPlanOrThrow(planId);
        Page<AiQuestion> page = planQuestionRepository.findQuestionsByPlanId(planId, pageable);
        var categories = questionService.categoriesForQuestions(page.getContent().stream().map(AiQuestion::getId).toList());
        return page.map(q -> AiQuestionSummaryResponse.from(q, categories.getOrDefault(q.getId(), List.of()), false));
    }

    @Transactional(readOnly = true)
    public Page<AiQuestionSummaryResponse> getAccessibleQuestions(
            Long userId, Long categoryId, String difficulty, String search, Pageable pageable) {
        List<Long> accessibleIds = accessibleQuestionIds(userId);
        if (accessibleIds.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        Page<AiQuestion> page = questionService.searchWithin(accessibleIds, categoryId, difficulty, search, pageable);
        var categories = questionService.categoriesForQuestions(page.getContent().stream().map(AiQuestion::getId).toList());
        Set<Long> bookmarked = bookmarkRepository.findBookmarkedQuestionIds(
                userId, page.getContent().stream().map(AiQuestion::getId).toList());
        return page.map(q -> AiQuestionSummaryResponse.from(
                q, categories.getOrDefault(q.getId(), List.of()), bookmarked.contains(q.getId())));
    }

    @Transactional(readOnly = true)
    public AiQuestionDetailResponse getAccessibleQuestionDetail(Long userId, Long questionId) {
        List<Long> accessibleIds = accessibleQuestionIds(userId);
        if (!accessibleIds.contains(questionId)) {
            throw new AccessDeniedException("This question is not included in your current subscription plan");
        }
        AiQuestion question = questionService.findQuestionOrThrow(questionId);
        boolean bookmarked = bookmarkRepository.existsByUser_IdAndQuestion_Id(userId, questionId);
        return AiQuestionDetailResponse.from(question, questionService.categoriesFor(questionId), bookmarked);
    }

    /**
     * The user's plan-accessible AI-question ids: must be an explicit ai_plan_question row for
     * their active plan AND rank within the plan's questionLimit when ordered by question id.
     */
    private List<Long> accessibleQuestionIds(Long userId) {
        UserSubscription subscription = subscriptionRepository
                .findByUserIdAndSubscriptionStatus(userId, SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("No active subscription for user: " + userId));

        Long planId = subscription.getPlan().getPlanId();
        int effectiveTotal = Math.min(subscription.getPlan().getQuestionLimit(),
                (int) planQuestionRepository.countByPlan_PlanId(planId));

        List<Long> orderedIds = planQuestionRepository.findQuestionIdsByPlanIdOrderByQuestionIdAsc(planId);
        return orderedIds.size() > effectiveTotal ? orderedIds.subList(0, effectiveTotal) : orderedIds;
    }
}
