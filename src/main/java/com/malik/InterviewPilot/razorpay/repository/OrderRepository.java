package com.malik.InterviewPilot.razorpay.repository;

import com.malik.InterviewPilot.razorpay.entity.OrderStatus;
import com.malik.InterviewPilot.razorpay.entity.PaymentOrder;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<PaymentOrder, Long> {

    Optional<PaymentOrder> findByRazorpayOrderId(String razorpayOrderId);

    /**
     * Locks the order row for the duration of the caller's transaction so two
     * concurrent verify requests for the same order can't both race past the
     * "already processed?" check in PaymentVerificationService.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from PaymentOrder o where o.razorpayOrderId = :razorpayOrderId")
    Optional<PaymentOrder> findByRazorpayOrderIdForUpdate(@Param("razorpayOrderId") String razorpayOrderId);

    Optional<PaymentOrder> findFirstByUserIdAndPlanPlanIdAndStatusAndCreatedAtAfterOrderByCreatedAtDesc(
            Long userId, Long planId, OrderStatus status, Instant createdAfter);
}
