package com.malik.InterviewPilot.razorpay.dto;

import jakarta.validation.constraints.NotBlank;

public record PaymentVerificationRequest(
        @NotBlank(message = "razorpayOrderId is required") String razorpayOrderId,
        @NotBlank(message = "razorpayPaymentId is required") String razorpayPaymentId,
        @NotBlank(message = "razorpaySignature is required") String razorpaySignature
) {
}
