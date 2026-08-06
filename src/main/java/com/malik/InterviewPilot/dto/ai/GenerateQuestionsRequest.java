package com.malik.InterviewPilot.dto.ai;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record GenerateQuestionsRequest(
        @NotBlank(message = "Technology is required") String technology,
        @NotBlank(message = "Experience level is required") String experienceLevel,
        @Min(1) @Max(20) int count
) {
}
