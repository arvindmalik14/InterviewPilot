package com.malik.InterviewPilot.razorpay.service;

import com.malik.InterviewPilot.entity.User;
import com.malik.InterviewPilot.razorpay.entity.SubscriptionPlan;
import com.malik.InterviewPilot.razorpay.entity.SubscriptionStatus;
import com.malik.InterviewPilot.razorpay.entity.UserSubscription;
import com.malik.InterviewPilot.razorpay.repository.SubscriptionPlanRepository;
import com.malik.InterviewPilot.razorpay.repository.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SubscriptionServiceTest {

    private SubscriptionRepository subscriptionRepository;
    private SubscriptionPlanRepository subscriptionPlanRepository;
    private SubscriptionService subscriptionService;

    @BeforeEach
    void setUp() {
        subscriptionRepository = mock(SubscriptionRepository.class);
        subscriptionPlanRepository = mock(SubscriptionPlanRepository.class);
        subscriptionService = new SubscriptionService(subscriptionRepository, subscriptionPlanRepository);
    }

    @Test
    void activateSubscription_createsNewSubscription_whenNoneExists() {
        User user = User.builder().id(1L).build();
        SubscriptionPlan plan = SubscriptionPlan.builder().planId(2L).durationInMonths(1).questionLimit(500).build();

        when(subscriptionRepository.findByUserIdAndSubscriptionStatus(1L, SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.empty());
        when(subscriptionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserSubscription result = subscriptionService.activateSubscription(user, plan);

        assertEquals(LocalDate.now(), result.getStartDate());
        assertEquals(LocalDate.now().plusMonths(1), result.getEndDate());
        assertEquals(SubscriptionStatus.ACTIVE, result.getSubscriptionStatus());
        assertEquals(500, result.getRemainingQuestionCount());
    }

    @Test
    void activateSubscription_extendsFromCurrentEndDate_whenRenewingSamePlanBeforeExpiry() {
        User user = User.builder().id(1L).build();
        SubscriptionPlan plan = SubscriptionPlan.builder().planId(2L).durationInMonths(3).questionLimit(1000).build();
        LocalDate futureEnd = LocalDate.now().plusDays(10);
        UserSubscription existing = UserSubscription.builder()
                .user(user)
                .plan(plan)
                .startDate(LocalDate.now().minusMonths(1))
                .endDate(futureEnd)
                .subscriptionStatus(SubscriptionStatus.ACTIVE)
                .remainingQuestionCount(200)
                .build();

        when(subscriptionRepository.findByUserIdAndSubscriptionStatus(1L, SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.of(existing));
        when(subscriptionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserSubscription result = subscriptionService.activateSubscription(user, plan);

        // Extends from the existing end date, not from today — a renewal never loses paid-for time.
        assertEquals(futureEnd.plusMonths(3), result.getEndDate());
        assertEquals(1000, result.getRemainingQuestionCount());
    }

    @Test
    void activateSubscription_restartsFromToday_whenPreviousTermAlreadyExpired() {
        User user = User.builder().id(1L).build();
        SubscriptionPlan plan = SubscriptionPlan.builder().planId(2L).durationInMonths(1).questionLimit(500).build();
        LocalDate pastEnd = LocalDate.now().minusDays(5);
        UserSubscription existing = UserSubscription.builder()
                .user(user)
                .plan(plan)
                .startDate(LocalDate.now().minusMonths(2))
                .endDate(pastEnd)
                .subscriptionStatus(SubscriptionStatus.ACTIVE)
                .remainingQuestionCount(0)
                .build();

        when(subscriptionRepository.findByUserIdAndSubscriptionStatus(1L, SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.of(existing));
        when(subscriptionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserSubscription result = subscriptionService.activateSubscription(user, plan);

        assertEquals(LocalDate.now().plusMonths(1), result.getEndDate());
    }

    @Test
    void activateSubscription_deactivatesPreviousPlan_whenSwitchingToADifferentPlan() {
        User user = User.builder().id(1L).build();
        SubscriptionPlan oldPlan = SubscriptionPlan.builder().planId(1L).durationInMonths(12).questionLimit(50).build();
        SubscriptionPlan newPlan = SubscriptionPlan.builder().planId(3L).durationInMonths(12).questionLimit(2000).build();
        UserSubscription existingFree = UserSubscription.builder()
                .user(user)
                .plan(oldPlan)
                .startDate(LocalDate.now().minusMonths(2))
                .endDate(LocalDate.now().plusMonths(10))
                .subscriptionStatus(SubscriptionStatus.ACTIVE)
                .remainingQuestionCount(50)
                .build();

        when(subscriptionRepository.findByUserIdAndSubscriptionStatus(1L, SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.of(existingFree));
        when(subscriptionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserSubscription result = subscriptionService.activateSubscription(user, newPlan);

        // The old plan's row is saved with CANCELLED status (superseded, not naturally expired)...
        verify(subscriptionRepository).save(existingFree);
        assertEquals(SubscriptionStatus.CANCELLED, existingFree.getSubscriptionStatus());
        // ...and a fresh term starts today for the new plan, not extended from the old plan's end date.
        assertEquals(LocalDate.now(), result.getStartDate());
        assertEquals(LocalDate.now().plusMonths(12), result.getEndDate());
        assertEquals(2000, result.getRemainingQuestionCount());
        assertEquals(SubscriptionStatus.ACTIVE, result.getSubscriptionStatus());
    }

    @Test
    void assignFreePlan_activatesTheSeededFreePlan() {
        User user = User.builder().id(1L).build();
        SubscriptionPlan freePlan = SubscriptionPlan.builder().planId(1L).planName("Free").durationInMonths(12).questionLimit(50).build();

        when(subscriptionPlanRepository.findByPlanName("Free")).thenReturn(Optional.of(freePlan));
        when(subscriptionRepository.findByUserIdAndSubscriptionStatus(1L, SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.empty());
        when(subscriptionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserSubscription result = subscriptionService.assignFreePlan(user);

        assertEquals("Free", result.getPlan().getPlanName());
        assertEquals(SubscriptionStatus.ACTIVE, result.getSubscriptionStatus());
    }

    @Test
    void assignFreePlan_throwsIfFreePlanNotSeeded() {
        when(subscriptionPlanRepository.findByPlanName("Free")).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> subscriptionService.assignFreePlan(User.builder().id(1L).build()));
    }

    @Test
    void findLatestActiveOrThrow_throwsWhenMissing() {
        when(subscriptionRepository.findByUserAndPlanAndStatus(1L, 2L, SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> subscriptionService.findLatestActiveOrThrow(1L, 2L));
    }
}
