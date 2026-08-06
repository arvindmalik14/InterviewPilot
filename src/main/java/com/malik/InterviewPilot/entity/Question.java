package com.malik.InterviewPilot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "questions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @Column(nullable = false, length = 2000)
    private String question;

    @Column(nullable = false, length = 1000)
    private String optionA;

    @Column(nullable = false, length = 1000)
    private String optionB;

    @Column(nullable = false, length = 1000)
    private String optionC;

    @Column(nullable = false, length = 1000)
    private String optionD;

    @Column(nullable = false, length = 10)
    private String answer;

    @Column(length = 2000)
    private String explanation;

    @Column(length = 20)
    @Builder.Default
    private String difficulty = "MEDIUM";
}
