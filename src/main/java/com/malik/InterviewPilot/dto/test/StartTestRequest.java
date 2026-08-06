package com.malik.InterviewPilot.dto.test;

import jakarta.validation.constraints.NotNull;

public record StartTestRequest(
        @NotNull(message = "Exam id is required") Long examId,
        Integer questionCount
) {
}
