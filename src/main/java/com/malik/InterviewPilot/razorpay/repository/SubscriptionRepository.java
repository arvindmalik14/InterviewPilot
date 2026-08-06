package com.malik.InterviewPilot.razorpay.repository;

import com.malik.InterviewPilot.razorpay.entity.SubscriptionStatus;
import com.malik.InterviewPilot.razorpay.entity.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<UserSubscription, Long> {

    @Query("select s from UserSubscription s where s.user.id = :userId and s.plan.planId = :planId "
            + "and s.subscriptionStatus = :status")
    Optional<UserSubscription> findByUserAndPlanAndStatus(
            @Param("userId") Long userId, @Param("planId") Long planId, @Param("status") SubscriptionStatus status);

    /** A user has at most one active subscription regardless of plan — see SubscriptionService. */
    Optional<UserSubscription> findByUserIdAndSubscriptionStatus(Long userId, SubscriptionStatus status);

    List<UserSubscription> findByUserIdOrderByEndDateDesc(Long userId);
}
