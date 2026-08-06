package com.malik.InterviewPilot.razorpay.exception;

/** Wraps failures talking to the Razorpay API itself (after retries are exhausted). */
public class RazorpayIntegrationException extends RuntimeException {
    public RazorpayIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
