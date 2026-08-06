package com.malik.InterviewPilot.aiqa.dto.category;

import com.malik.InterviewPilot.aiqa.entity.AiCategory;
import com.malik.InterviewPilot.aiqa.entity.AiContentStatus;

import java.time.Instant;

public record AiCategoryResponse(
        Long id,
        String name,
        String description,
        AiContentStatus status,
        long questionCount,
        Instant createdAt,
        Instant updatedAt
) {
    public static AiCategoryResponse from(AiCategory category, long questionCount) {
        return new AiCategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getStatus(),
                questionCount,
                category.getCreatedAt(),
                category.getUpdatedAt());
    }
}
