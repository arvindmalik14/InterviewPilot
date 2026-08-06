package com.malik.InterviewPilot.razorpay.service;

import com.malik.InterviewPilot.entity.User;
import com.malik.InterviewPilot.razorpay.dto.SubscriptionResponse;
import com.malik.InterviewPilot.razorpay.entity.SubscriptionPlan;
import com.malik.InterviewPilot.razorpay.entity.SubscriptionStatus;
import com.malik.InterviewPilot.razorpay.entity.UserSubscription;
import com.malik.InterviewPilot.razorpay.repository.SubscriptionPlanRepository;
import com.malik.InterviewPilot.razorpay.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionService {

    public static final String FREE_PLAN_NAME = "Free";

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;

    /**
     * A user holds exactly one active plan at a time. If they already have an active
     * subscription for a *different* plan, it's deactivated (CANCELLED — superseded, not
     * naturally expired) and a fresh subscription starts today for the new plan. If the
     * active subscription is for this *same* plan (a renewal), it's extended from its
     * current end date rather than from today, so a renewal never shortens time the user
     * already paid for.
     */
    public UserSubscription activateSubscription(User user, SubscriptionPlan plan) {
        LocalDate today = LocalDate.now();
        Optional<UserSubscription> currentActive =
                subscriptionRepository.findByUserIdAndSubscriptionStatus(user.getId(), SubscriptionStatus.ACTIVE);

        boolean isRenewalOfSamePlan = currentActive.isPresent()
                && currentActive.get().getPlan().getPlanId().equals(plan.getPlanId());

        if (currentActive.isPresent() && !isRenewalOfSamePlan) {
            UserSubscription previous = currentActive.get();
            previous.setSubscriptionStatus(SubscriptionStatus.CANCELLED);
            subscriptionRepository.save(previous);
            currentActive = Optional.empty();
        }

        UserSubscription subscription = currentActive.orElseGet(() -> UserSubscription.builder()
                .user(user)
                .plan(plan)
                .startDate(today)
                .subscriptionStatus(SubscriptionStatus.ACTIVE)
                .build());

        boolean extendingActiveTerm = isRenewalOfSamePlan && subscription.getEndDate().isAfter(today);
        LocalDate extendFrom = extendingActiveTerm ? subscription.getEndDate() : today;

        subscription.setPlan(plan);
        subscription.setEndDate(extendFrom.plusMonths(plan.getDurationInMonths()));
        subscription.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
        subscription.setRemainingQuestionCount(plan.getQuestionLimit());

        return subscriptionRepository.save(subscription);
    }

    /** Every newly registered user starts on the Free plan — no payment involved. */
    public UserSubscription assignFreePlan(User user) {
        SubscriptionPlan freePlan = subscriptionPlanRepository.findByPlanName(FREE_PLAN_NAME)
                .orElseThrow(() -> new IllegalStateException("Free plan not found — has SubscriptionPlanSeeder run?"));
        return activateSubscription(user, freePlan);
    }

    public UserSubscription findLatestActiveOrThrow(Long userId, Long planId) {
        return subscriptionRepository.findByUserAndPlanAndStatus(userId, planId, SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new IllegalStateException(
                        "Expected an active subscription for user " + userId + " on plan " + planId));
    }

    /** Mapped to DTOs inside this transactional method so the caller never touches lazy proxies. */
    public List<SubscriptionResponse> getSubscriptionsForUser(Long userId) {
        return subscriptionRepository.findByUserIdOrderByEndDateDesc(userId).stream()
                .map(SubscriptionResponse::from)
                .toList();
    }
}
