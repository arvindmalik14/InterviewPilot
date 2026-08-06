package com.malik.InterviewPilot.aiqa.dto.question;

import com.malik.InterviewPilot.aiqa.entity.AiCategory;

public record CategorySummary(Long id, String name) {
    public static CategorySummary from(AiCategory category) {
        return new CategorySummary(category.getId(), category.getName());
    }
}
