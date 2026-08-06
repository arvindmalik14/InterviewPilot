package com.malik.InterviewPilot.aiqa.entity;

import com.malik.InterviewPilot.razorpay.entity.SubscriptionPlan;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "ai_plan_question",
        uniqueConstraints = @UniqueConstraint(name = "uk_ai_plan_question", columnNames = {"plan_id", "question_id"}),
        indexes = {
                @Index(name = "idx_ai_plan_question_plan_id", columnList = "plan_id"),
                @Index(name = "idx_ai_plan_question_question_id", columnList = "question_id")
        })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiPlanQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private SubscriptionPlan plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private AiQuestion question;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();
}
