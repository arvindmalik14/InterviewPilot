package com.malik.InterviewPilot.dto.ai;

import jakarta.validation.constraints.NotBlank;

public record CodeReviewRequest(
        @NotBlank(message = "Code is required") String code,
        String language
) {
}
