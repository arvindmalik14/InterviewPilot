package com.malik.InterviewPilot.dto.question;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record QuestionRequest(
        @NotNull(message = "Exam id is required") Long examId,
        @NotBlank(message = "Question text is required") String question,
        @NotBlank String optionA,
        @NotBlank String optionB,
        @NotBlank String optionC,
        @NotBlank String optionD,
        @NotBlank(message = "Answer is required") String answer,
        String explanation,
        String difficulty
) {
}
