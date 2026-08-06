package com.malik.InterviewPilot.razorpay.entity;

import com.malik.InterviewPilot.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * One row per verified Razorpay payment callback. The unique constraint on
 * {@code razorpay_payment_id} is the last line of defense against double-processing
 * a payment under concurrent/duplicate verify calls — see PaymentVerificationService.
 */
@Entity
@Table(name = "payment_transaction", uniqueConstraints = {
        @UniqueConstraint(name = "uk_payment_transaction_razorpay_payment_id", columnNames = "razorpay_payment_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private PaymentOrder order;

    @Column(name = "razorpay_payment_id", nullable = false, length = 100)
    private String razorpayPaymentId;

    @Column(name = "razorpay_signature", nullable = false, length = 255)
    private String razorpaySignature;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionStatus status;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private Instant updatedAt = Instant.now();

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
