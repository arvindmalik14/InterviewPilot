package com.malik.InterviewPilot.razorpay.controller;

import com.malik.InterviewPilot.entity.Role;
import com.malik.InterviewPilot.razorpay.dto.CreateOrderRequest;
import com.malik.InterviewPilot.razorpay.dto.CreateOrderResponse;
import com.malik.InterviewPilot.razorpay.dto.PaymentVerificationRequest;
import com.malik.InterviewPilot.razorpay.dto.PaymentVerificationResponse;
import com.malik.InterviewPilot.razorpay.service.PaymentVerificationService;
import com.malik.InterviewPilot.razorpay.service.RazorpayService;
import com.malik.InterviewPilot.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class RazorpayController {

    private final RazorpayService razorpayService;
    private final PaymentVerificationService paymentVerificationService;

    @PostMapping("/order")
    public ResponseEntity<CreateOrderResponse> createOrder(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateOrderRequest request) {
        assertSelfOrAdmin(principal, request.userId());
        return ResponseEntity.status(HttpStatus.CREATED).body(razorpayService.createOrder(request));
    }

    @PostMapping("/verify")
    public ResponseEntity<PaymentVerificationResponse> verifyPayment(
            @Valid @RequestBody PaymentVerificationRequest request) {
        return ResponseEntity.ok(paymentVerificationService.verifyPayment(request));
    }

    /** Prevents one user from creating an order on another user's behalf (IDOR guard). */
    private void assertSelfOrAdmin(UserPrincipal principal, Long requestedUserId) {
        boolean isAdmin = principal.getUser().getRole() == Role.ADMIN;
        if (!isAdmin && !principal.getUser().getId().equals(requestedUserId)) {
            throw new AccessDeniedException("You may only create an order for your own account");
        }
    }
}
