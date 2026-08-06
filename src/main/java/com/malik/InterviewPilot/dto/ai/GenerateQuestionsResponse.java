package com.malik.InterviewPilot.dto.ai;

import java.util.List;

public record GenerateQuestionsResponse(
        String technology,
        String experienceLevel,
        List<GeneratedQuestion> questions
) {
}
