package com.malik.InterviewPilot.razorpay.dto;

import com.malik.InterviewPilot.razorpay.entity.PlanStatus;
import com.malik.InterviewPilot.razorpay.entity.SubscriptionPlan;

import java.math.BigDecimal;
import java.time.Instant;

/** Admin-facing plan view — exposes isActive (derived from the internal PlanStatus enum). */
public record PlanAdminResponse(
        Long planId,
        String planName,
        BigDecimal price,
        Integer durationInMonths,
        Integer questionLimit,
        boolean isActive,
        Instant createdAt,
        Instant updatedAt
) {
    public static PlanAdminResponse from(SubscriptionPlan plan) {
        return new PlanAdminResponse(
                plan.getPlanId(), plan.getPlanName(), plan.getPrice(), plan.getDurationInMonths(),
                plan.getQuestionLimit(), plan.getStatus() == PlanStatus.ACTIVE, plan.getCreatedAt(), plan.getUpdatedAt());
    }
}
