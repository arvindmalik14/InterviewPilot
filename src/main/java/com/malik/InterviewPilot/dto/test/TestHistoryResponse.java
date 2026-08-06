package com.malik.InterviewPilot.dto.test;

import com.malik.InterviewPilot.entity.TestAttempt;

import java.time.Instant;

public record TestHistoryResponse(
        Long id,
        String examName,
        Integer score,
        Integer totalQuestions,
        Integer durationSeconds,
        String status,
        Instant startedAt,
        Instant completedAt
) {
    public static TestHistoryResponse from(TestAttempt attempt) {
        return new TestHistoryResponse(
                attempt.getId(), attempt.getExam().getName(), attempt.getScore(),
                attempt.getTotalQuestions(), attempt.getDurationSeconds(), attempt.getStatus(),
                attempt.getStartedAt(), attempt.getCompletedAt());
    }
}
