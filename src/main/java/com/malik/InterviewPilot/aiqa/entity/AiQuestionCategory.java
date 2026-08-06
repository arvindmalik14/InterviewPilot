package com.malik.InterviewPilot.aiqa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ai_question_category",
        uniqueConstraints = @UniqueConstraint(name = "uk_ai_question_category", columnNames = {"question_id", "category_id"}),
        indexes = {
                @Index(name = "idx_ai_question_category_question_id", columnList = "question_id"),
                @Index(name = "idx_ai_question_category_category_id", columnList = "category_id")
        })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiQuestionCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private AiQuestion question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private AiCategory category;
}
