package com.malik.InterviewPilot.razorpay.config;

import com.malik.InterviewPilot.razorpay.entity.PlanStatus;
import com.malik.InterviewPilot.razorpay.entity.SubscriptionPlan;
import com.malik.InterviewPilot.razorpay.repository.SubscriptionPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Seeds the four subscription tiers. Question limits below are illustrative — adjust to
 * the real business terms. All four carry a 12-month validity per the current business
 * rules; Free is auto-assigned at registration (see AuthService) rather than purchased.
 */
@Component
@RequiredArgsConstructor
@Order(10)
public class SubscriptionPlanSeeder implements CommandLineRunner {

    public static final String FREE_PLAN_NAME = "Free";

    private final SubscriptionPlanRepository subscriptionPlanRepository;

    @Override
    public void run(String... args) {
        if (subscriptionPlanRepository.count() > 0) {
            return;
        }

        subscriptionPlanRepository.saveAll(List.of(
                plan(FREE_PLAN_NAME, new BigDecimal("0.00"), 12, 50),
                plan("Basic", new BigDecimal("99.00"), 12, 500),
                plan("Premium", new BigDecimal("299.00"), 12, 2000),
                plan("Enterprise", new BigDecimal("999.00"), 12, 10000)));
    }

    private SubscriptionPlan plan(String name, BigDecimal price, int durationInMonths, int questionLimit) {
        return SubscriptionPlan.builder()
                .planName(name)
                .price(price)
                .durationInMonths(durationInMonths)
                .questionLimit(questionLimit)
                .status(PlanStatus.ACTIVE)
                .build();
    }
}
