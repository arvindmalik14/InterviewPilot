package com.malik.InterviewPilot.dto.question;

import com.malik.InterviewPilot.entity.Question;

public record QuestionAdminResponse(
        Long id,
        Long examId,
        String question,
        String optionA,
        String optionB,
        String optionC,
        String optionD,
        String answer,
        String explanation,
        String difficulty
) {
    public static QuestionAdminResponse from(Question q) {
        return new QuestionAdminResponse(
                q.getId(), q.getExam().getId(), q.getQuestion(),
                q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD(),
                q.getAnswer(), q.getExplanation(), q.getDifficulty());
    }
}
