package com.malik.InterviewPilot.razorpay.dto;

import com.malik.InterviewPilot.razorpay.entity.UserSubscription;

import java.time.LocalDate;

public record SubscriptionResponse(
        Long userId,
        Long planId,
        String planName,
        LocalDate startDate,
        LocalDate endDate,
        String subscriptionStatus,
        Integer remainingQuestionCount
) {
    public static SubscriptionResponse from(UserSubscription subscription) {
        return new SubscriptionResponse(
                subscription.getUser().getId(),
                subscription.getPlan().getPlanId(),
                subscription.getPlan().getPlanName(),
                subscription.getStartDate(),
                subscription.getEndDate(),
                subscription.getSubscriptionStatus().name(),
                subscription.getRemainingQuestionCount());
    }
}
