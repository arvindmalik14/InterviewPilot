package com.malik.InterviewPilot.dto.test;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;

import java.util.List;

public record SubmitTestRequest(
        @NotNull(message = "Duration is required") Integer durationSeconds,
        @NotEmpty(message = "At least one answer is required") @Valid List<SubmitAnswerRequest> answers
) {
}
