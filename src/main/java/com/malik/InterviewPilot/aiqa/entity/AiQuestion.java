package com.malik.InterviewPilot.aiqa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "ai_questions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(name = "detailed_answer", nullable = false, columnDefinition = "TEXT")
    private String detailedAnswer;

    @Column(name = "real_world_example", columnDefinition = "TEXT")
    private String realWorldExample;

    @Column(name = "difficulty_level", nullable = false, length = 20)
    private String difficultyLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private AiContentStatus status = AiContentStatus.ACTIVE;

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
