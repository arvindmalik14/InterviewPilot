package com.malik.InterviewPilot.dto.question;

import com.malik.InterviewPilot.entity.Question;

/** Question shape shown to a candidate while a test is in progress — answer/explanation withheld. */
public record QuestionPublicResponse(
        Long id,
        String question,
        String optionA,
        String optionB,
        String optionC,
        String optionD,
        String difficulty
) {
    public static QuestionPublicResponse from(Question q) {
        return new QuestionPublicResponse(
                q.getId(), q.getQuestion(), q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD(), q.getDifficulty());
    }
}
