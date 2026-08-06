package com.malik.InterviewPilot.razorpay.entity;

import com.malik.InterviewPilot.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * One row per Razorpay order we create. {@code amount} is stored in the smallest
 * currency unit (paise for INR) — the same unit Razorpay's API expects — so no
 * conversion is needed when reconciling against Razorpay dashboard data.
 */
@Entity
@Table(name = "payment_order", uniqueConstraints = {
        @UniqueConstraint(name = "uk_payment_order_razorpay_order_id", columnNames = "razorpay_order_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private SubscriptionPlan plan;

    @Column(name = "razorpay_order_id", nullable = false, length = 100)
    private String razorpayOrderId;

    /** Amount in the smallest currency unit (paise), matching what was sent to Razorpay. */
    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false, length = 10)
    @Builder.Default
    private String currency = "INR";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private OrderStatus status = OrderStatus.CREATED;

    @Column(nullable = false, length = 100)
    private String receipt;

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
