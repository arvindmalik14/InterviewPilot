package com.malik.InterviewPilot.aiqa.dto.question;

import com.malik.InterviewPilot.aiqa.entity.AiContentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record AiQuestionRequest(
        @NotBlank(message = "Title is required") @Size(max = 500, message = "Title must be at most 500 characters") String title,
        @NotBlank(message = "Detailed answer is required") String detailedAnswer,
        String realWorldExample,
        @NotBlank(message = "Difficulty level is required") @Size(max = 20) String difficultyLevel,
        AiContentStatus status,
        @NotEmpty(message = "At least one category must be assigned") List<@NotNull Long> categoryIds
) {
}
