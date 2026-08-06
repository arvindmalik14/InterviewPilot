package com.malik.InterviewPilot.razorpay.dto;

public record PaymentVerificationResponse(
        boolean success,
        String message,
        SubscriptionResponse subscription
) {
}
