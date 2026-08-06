package com.malik.InterviewPilot.razorpay.dto;

/**
 * When {@code paymentRequired} is false (a zero-price plan, e.g. Free), the plan has
 * already been activated and the Razorpay-specific fields are null — the frontend should
 * skip opening the Checkout widget entirely.
 */
public record CreateOrderResponse(
        boolean paymentRequired,
        String razorpayOrderId,
        String razorpayKeyId,
        Long amount,
        String currency,
        String planName,
        String receipt
) {
}
