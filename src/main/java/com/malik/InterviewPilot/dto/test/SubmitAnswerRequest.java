package com.malik.InterviewPilot.dto.test;

import jakarta.validation.constraints.NotNull;

public record SubmitAnswerRequest(
        @NotNull(message = "Question id is required") Long questionId,
        String selectedOption
) {
}
