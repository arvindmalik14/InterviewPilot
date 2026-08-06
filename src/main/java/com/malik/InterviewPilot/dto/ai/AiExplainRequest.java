package com.malik.InterviewPilot.dto.ai;

import jakarta.validation.constraints.NotNull;

public record AiExplainRequest(
        @NotNull(message = "Question id is required") Long questionId
) {
}
