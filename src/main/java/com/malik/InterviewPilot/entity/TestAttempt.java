package com.malik.InterviewPilot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @Column
    private Integer score;

    @Column(nullable = false)
    private Integer totalQuestions;

    @Column
    private Integer durationSeconds;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "IN_PROGRESS";

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private Instant startedAt = Instant.now();

    @Column
    private Instant completedAt;

    @OneToMany(mappedBy = "testAttempt", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TestAnswer> answers = new ArrayList<>();
}
