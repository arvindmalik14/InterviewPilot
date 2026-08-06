package com.malik.InterviewPilot.razorpay.controller;

import com.malik.InterviewPilot.razorpay.dto.SubscriptionResponse;
import com.malik.InterviewPilot.razorpay.service.SubscriptionService;
import com.malik.InterviewPilot.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping("/me")
    public List<SubscriptionResponse> getMySubscriptions(@AuthenticationPrincipal UserPrincipal principal) {
        return subscriptionService.getSubscriptionsForUser(principal.getUser().getId());
    }
}
