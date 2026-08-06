package com.malik.InterviewPilot.aiqa.dto.question;

import com.malik.InterviewPilot.aiqa.entity.AiQuestion;

import java.util.List;

/** The "AI Explanation" reveal — full detailed answer + real-world example. */
public record AiQuestionDetailResponse(
        Long id,
        String title,
        String detailedAnswer,
        String realWorldExample,
        String difficultyLevel,
        List<CategorySummary> categories,
        boolean bookmarked
) {
    public static AiQuestionDetailResponse from(AiQuestion question, List<CategorySummary> categories, boolean bookmarked) {
        return new AiQuestionDetailResponse(
                question.getId(),
                question.getTitle(),
                question.getDetailedAnswer(),
                question.getRealWorldExample(),
                question.getDifficultyLevel(),
                categories,
                bookmarked);
    }
}
