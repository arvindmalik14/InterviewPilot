package com.malik.InterviewPilot.razorpay.repository;

import com.malik.InterviewPilot.razorpay.entity.PlanStatus;
import com.malik.InterviewPilot.razorpay.entity.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {
    List<SubscriptionPlan> findByStatus(PlanStatus status);
    Optional<SubscriptionPlan> findByPlanName(String planName);
}
