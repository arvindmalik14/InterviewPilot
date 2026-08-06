package com.malik.InterviewPilot.razorpay.service;

import com.malik.InterviewPilot.dto.question.QuestionAdminResponse;
import com.malik.InterviewPilot.dto.question.QuestionPublicResponse;
import com.malik.InterviewPilot.entity.Question;
import com.malik.InterviewPilot.exception.DuplicateResourceException;
import com.malik.InterviewPilot.exception.ResourceNotFoundException;
import com.malik.InterviewPilot.razorpay.entity.PlanQuestion;
import com.malik.InterviewPilot.razorpay.entity.SubscriptionPlan;
import com.malik.InterviewPilot.razorpay.entity.SubscriptionStatus;
import com.malik.InterviewPilot.razorpay.entity.UserSubscription;
import com.malik.InterviewPilot.razorpay.repository.PlanQuestionRepository;
import com.malik.InterviewPilot.razorpay.repository.SubscriptionRepository;
import com.malik.InterviewPilot.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Admin question<->plan assignment, and the enforcement point for a user's plan-scoped question access. */
@Service
@RequiredArgsConstructor
@Transactional
public class PlanQuestionService {

    private final PlanQuestionRepository planQuestionRepository;
    private final PlanService planService;
    private final QuestionService questionService;
    private final SubscriptionRepository subscriptionRepository;

    public void assignQuestionToPlan(Long planId, Long questionId) {
        SubscriptionPlan plan = planService.findPlanOrThrow(planId);
        Question question = questionService.findQuestionOrThrow(questionId);

        if (planQuestionRepository.existsByPlan_PlanIdAndQuestion_Id(planId, questionId)) {
            throw new DuplicateResourceException(
                    "Question " + questionId + " is already assigned to plan " + planId);
        }

        planQuestionRepository.save(PlanQuestion.builder().plan(plan).question(question).build());
    }

    public void removeQuestionFromPlan(Long planId, Long questionId) {
        PlanQuestion assignment = planQuestionRepository.findByPlan_PlanIdAndQuestion_Id(planId, questionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Question " + questionId + " is not assigned to plan " + planId));
        planQuestionRepository.delete(assignment);
    }

    @Transactional(readOnly = true)
    public Page<QuestionAdminResponse> listQuestionsForPlan(Long planId, Pageable pageable) {
        planService.findPlanOrThrow(planId);
        return planQuestionRepository.findQuestionsByPlanId(planId, pageable).map(QuestionAdminResponse::from);
    }

    /**
     * Resolves the user's active plan, caps the total questions they can see at that plan's
     * question_limit (regardless of how many are actually assigned to the plan), and paginates
     * within that cap. This is what stops a user from paging past their plan's limit, or from
     * ever seeing a question not assigned to their plan.
     */
    @Transactional(readOnly = true)
    public Page<QuestionPublicResponse> getAccessibleQuestionsForUser(Long userId, Pageable pageable) {
        UserSubscription subscription = subscriptionRepository
                .findByUserIdAndSubscriptionStatus(userId, SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("No active subscription for user: " + userId));

        Long planId = subscription.getPlan().getPlanId();
        long assignedCount = planQuestionRepository.countByPlan_PlanId(planId);
        long effectiveTotal = Math.min(subscription.getPlan().getQuestionLimit(), assignedCount);

        long offset = pageable.getOffset();
        if (offset >= effectiveTotal) {
            return new PageImpl<>(List.of(), pageable, effectiveTotal);
        }

        Page<Question> page = planQuestionRepository.findQuestionsByPlanId(planId, pageable);
        List<Question> content = page.getContent();
        long remainingWithinLimit = effectiveTotal - offset;
        if (content.size() > remainingWithinLimit) {
            content = content.subList(0, (int) remainingWithinLimit);
        }

        List<QuestionPublicResponse> dtos = content.stream().map(QuestionPublicResponse::from).toList();
        return new PageImpl<>(dtos, pageable, effectiveTotal);
    }
}
