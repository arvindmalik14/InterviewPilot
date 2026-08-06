package com.malik.InterviewPilot.razorpay.exception;

import com.malik.InterviewPilot.exception.ErrorResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Scoped to the razorpay controller package, with explicit highest precedence.
 *
 * <p>Spring picks the first applicable {@code @ControllerAdvice} bean (in order) that has
 * <em>any</em> matching {@code @ExceptionHandler} — it does not compare specificity across
 * advice beans. Without an explicit order here, the unscoped GlobalExceptionHandler's
 * {@code Exception.class} catch-all can win first (by bean registration order) and shadow
 * these more specific handlers, turning e.g. AccessDeniedException into a generic 500.
 */
@RestControllerAdvice(basePackages = "com.malik.InterviewPilot.razorpay.controller")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RazorpayExceptionHandler {

    @ExceptionHandler(SubscriptionPlanNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePlanNotFound(SubscriptionPlanNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFound(OrderNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
    }

    @ExceptionHandler(InvalidPaymentSignatureException.class)
    public ResponseEntity<ErrorResponse> handleInvalidSignature(InvalidPaymentSignatureException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }

    @ExceptionHandler(DuplicatePaymentException.class)
    public ResponseEntity<ErrorResponse> handleDuplicatePayment(DuplicatePaymentException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of(HttpStatus.CONFLICT.value(), ex.getMessage()));
    }

    @ExceptionHandler(RazorpayIntegrationException.class)
    public ResponseEntity<ErrorResponse> handleIntegrationFailure(RazorpayIntegrationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(ErrorResponse.of(HttpStatus.BAD_GATEWAY.value(), ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.of(HttpStatus.FORBIDDEN.value(), ex.getMessage()));
    }
}
