package com.malik.InterviewPilot.razorpay.service;

import com.malik.InterviewPilot.exception.DuplicateResourceException;
import com.malik.InterviewPilot.exception.ResourceNotFoundException;
import com.malik.InterviewPilot.razorpay.dto.PlanAdminResponse;
import com.malik.InterviewPilot.razorpay.dto.PlanRequest;
import com.malik.InterviewPilot.razorpay.entity.PlanStatus;
import com.malik.InterviewPilot.razorpay.entity.SubscriptionPlan;
import com.malik.InterviewPilot.razorpay.repository.SubscriptionPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PlanService {

    private final SubscriptionPlanRepository subscriptionPlanRepository;

    public PlanAdminResponse createPlan(PlanRequest request) {
        assertNameNotTaken(request.planName(), null);

        SubscriptionPlan plan = SubscriptionPlan.builder()
                .planName(request.planName())
                .price(request.price())
                .durationInMonths(request.durationInMonths())
                .questionLimit(request.questionLimit())
                .status(toStatus(request.isActive(), PlanStatus.ACTIVE))
                .build();

        return PlanAdminResponse.from(subscriptionPlanRepository.save(plan));
    }

    /** Also the activate/deactivate entry point — isActive flips the plan's status when supplied. */
    public PlanAdminResponse updatePlan(Long planId, PlanRequest request) {
        SubscriptionPlan plan = findPlanOrThrow(planId);
        assertNameNotTaken(request.planName(), planId);

        plan.setPlanName(request.planName());
        plan.setPrice(request.price());
        plan.setDurationInMonths(request.durationInMonths());
        plan.setQuestionLimit(request.questionLimit());
        plan.setStatus(toStatus(request.isActive(), plan.getStatus()));

        return PlanAdminResponse.from(subscriptionPlanRepository.save(plan));
    }

    public SubscriptionPlan findPlanOrThrow(Long planId) {
        return subscriptionPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found: " + planId));
    }

    private void assertNameNotTaken(String planName, Long excludingPlanId) {
        subscriptionPlanRepository.findByPlanName(planName)
                .filter(existing -> excludingPlanId == null || !existing.getPlanId().equals(excludingPlanId))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("A plan named '" + planName + "' already exists");
                });
    }

    private static PlanStatus toStatus(Boolean isActive, PlanStatus fallback) {
        if (isActive == null) {
            return fallback;
        }
        return isActive ? PlanStatus.ACTIVE : PlanStatus.INACTIVE;
    }
}
