package com.malik.InterviewPilot.razorpay.entity;

import com.malik.InterviewPilot.entity.Question;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/** Many-to-many join between a plan and the questions it grants access to. */
@Entity
@Table(name = "plan_question",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_plan_question_plan_id_question_id", columnNames = {"plan_id", "question_id"}),
        indexes = {
                @Index(name = "idx_plan_question_plan_id", columnList = "plan_id"),
                @Index(name = "idx_plan_question_question_id", columnList = "question_id")
        })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private SubscriptionPlan plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();
}
