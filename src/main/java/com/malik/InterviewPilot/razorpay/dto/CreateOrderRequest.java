package com.malik.InterviewPilot.razorpay.dto;

import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(
        @NotNull(message = "userId is required") Long userId,
        @NotNull(message = "planId is required") Long planId
) {
}
