package com.malik.InterviewPilot.dto.test;

import java.util.List;

public record TestResultResponse(
        Long testAttemptId,
        String examName,
        int score,
        int totalQuestions,
        int correctCount,
        int durationSeconds,
        List<AnswerResultResponse> answers
) {
}
