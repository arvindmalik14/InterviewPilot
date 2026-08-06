package com.malik.InterviewPilot.aiqa.dto.category;

import com.malik.InterviewPilot.aiqa.entity.AiContentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AiCategoryRequest(
        @NotBlank(message = "Category name is required") @Size(max = 100, message = "Category name must be at most 100 characters") String name,
        @Size(max = 500, message = "Description must be at most 500 characters") String description,
        AiContentStatus status
) {
}
