package com.malik.InterviewPilot.aiqa.dto.question;

import com.malik.InterviewPilot.aiqa.entity.AiQuestion;

import java.util.List;

/** Candidate-facing list-view shape — title only, no answer content (revealed via the detail endpoint). */
public record AiQuestionSummaryResponse(
        Long id,
        String title,
        String difficultyLevel,
        List<CategorySummary> categories,
        boolean bookmarked
) {
    public static AiQuestionSummaryResponse from(AiQuestion question, List<CategorySummary> categories, boolean bookmarked) {
        return new AiQuestionSummaryResponse(
                question.getId(), question.getTitle(), question.getDifficultyLevel(), categories, bookmarked);
    }
}
