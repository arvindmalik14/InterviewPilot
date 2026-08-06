package com.malik.InterviewPilot.dto.test;

import com.malik.InterviewPilot.dto.question.QuestionPublicResponse;
import com.malik.InterviewPilot.entity.TestAttempt;

import java.util.List;

public record TestAttemptResponse(
        Long id,
        Long examId,
        String examName,
        String status,
        Integer totalQuestions,
        List<QuestionPublicResponse> questions
) {
    public static TestAttemptResponse from(TestAttempt attempt, List<QuestionPublicResponse> questions) {
        return new TestAttemptResponse(
                attempt.getId(), attempt.getExam().getId(), attempt.getExam().getName(),
                attempt.getStatus(), attempt.getTotalQuestions(), questions);
    }
}
