package com.malik.InterviewPilot.dto.ai;

import java.util.List;

public record CodeReviewResponse(
        List<String> suggestions,
        String qualitySummary
) {
}
