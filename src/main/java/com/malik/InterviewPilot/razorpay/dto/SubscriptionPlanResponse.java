package com.malik.InterviewPilot.razorpay.dto;

import com.malik.InterviewPilot.razorpay.entity.SubscriptionPlan;

import java.math.BigDecimal;

public record SubscriptionPlanResponse(
        Long planId,
        String planName,
        BigDecimal price,
        Integer durationInMonths,
        Integer questionLimit
) {
    public static SubscriptionPlanResponse from(SubscriptionPlan plan) {
        return new SubscriptionPlanResponse(
                plan.getPlanId(), plan.getPlanName(), plan.getPrice(),
                plan.getDurationInMonths(), plan.getQuestionLimit());
    }
}
