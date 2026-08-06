package com.malik.InterviewPilot.dto.exam;

import jakarta.validation.constraints.NotBlank;

public record ExamRequest(
        @NotBlank(message = "Name is required") String name,
        @NotBlank(message = "Category is required") String category,
        String description
) {
}
