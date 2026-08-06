package com.malik.InterviewPilot.razorpay.repository;

import com.malik.InterviewPilot.razorpay.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    Optional<PaymentTransaction> findByRazorpayPaymentId(String razorpayPaymentId);
    boolean existsByOrderId(Long orderId);
}
