package com.malik.InterviewPilot.razorpay.controller;

import com.malik.InterviewPilot.razorpay.dto.SubscriptionPlanResponse;
import com.malik.InterviewPilot.razorpay.entity.PlanStatus;
import com.malik.InterviewPilot.razorpay.repository.SubscriptionPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Public plan browsing — no auth required, same as the exam catalog. */
@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
public class PlanController {

    private final SubscriptionPlanRepository subscriptionPlanRepository;

    @GetMapping
    public List<SubscriptionPlanResponse> listActivePlans() {
        return subscriptionPlanRepository.findByStatus(PlanStatus.ACTIVE).stream()
                .map(SubscriptionPlanResponse::from)
                .toList();
    }
}
