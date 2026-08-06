package com.malik.InterviewPilot.dto.ai;

import jakarta.validation.constraints.NotBlank;

public record ResumeAnalysisRequest(
        @NotBlank(message = "Resume text is required") String resumeText
) {
}
