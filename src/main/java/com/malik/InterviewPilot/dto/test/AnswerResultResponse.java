package com.malik.InterviewPilot.dto.test;

public record AnswerResultResponse(
        Long questionId,
        String question,
        String optionA,
        String optionB,
        String optionC,
        String optionD,
        String selectedOption,
        String correctOption,
        boolean correct,
        String explanation
) {
}
