package com.malik.InterviewPilot.dto.ai;

import java.util.List;

public record ResumeAnalysisResponse(
        int overallScore,
        List<String> strengths,
        List<String> improvements
) {
}
