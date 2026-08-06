package com.malik.InterviewPilot.dto.feedback;

import jakarta.validation.constraints.*;

public record FeedbackRequest(
        @NotBlank(message = "Name is required") @Size(max = 100, message = "Name must be at most 100 characters") String name,
        @NotBlank(message = "Email is required") @Email(message = "Email must be valid") String email,
        @NotBlank(message = "Category is required") String category,
        @NotNull(message = "Rating is required") @Min(value = 1, message = "Rating must be between 1 and 5") @Max(value = 5, message = "Rating must be between 1 and 5") Integer rating,
        @NotBlank(message = "Message is required") @Size(max = 2000, message = "Message must be at most 2000 characters") String message
) {
}
