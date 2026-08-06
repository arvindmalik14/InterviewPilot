package com.malik.InterviewPilot.dto.exam;

import com.malik.InterviewPilot.entity.Exam;

public record ExamResponse(
        Long id,
        String name,
        String category,
        String description,
        long questionCount
) {
    public static ExamResponse from(Exam exam, long questionCount) {
        return new ExamResponse(exam.getId(), exam.getName(), exam.getCategory(), exam.getDescription(), questionCount);
    }
}
