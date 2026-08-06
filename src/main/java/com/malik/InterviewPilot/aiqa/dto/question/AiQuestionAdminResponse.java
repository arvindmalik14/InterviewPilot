package com.malik.InterviewPilot.aiqa.dto.question;

import com.malik.InterviewPilot.aiqa.entity.AiContentStatus;
import com.malik.InterviewPilot.aiqa.entity.AiQuestion;

import java.time.Instant;
import java.util.List;

/** Admin management shape — includes status and timestamps, which candidates never see. */
public record AiQuestionAdminResponse(
        Long id,
        String title,
        String detailedAnswer,
        String realWorldExample,
        String difficultyLevel,
        AiContentStatus status,
        List<CategorySummary> categories,
        Instant createdAt,
        Instant updatedAt
) {
    public static AiQuestionAdminResponse from(AiQuestion question, List<CategorySummary> categories) {
        return new AiQuestionAdminResponse(
                question.getId(),
                question.getTitle(),
                question.getDetailedAnswer(),
                question.getRealWorldExample(),
                question.getDifficultyLevel(),
                question.getStatus(),
                categories,
                question.getCreatedAt(),
                question.getUpdatedAt());
    }
}
