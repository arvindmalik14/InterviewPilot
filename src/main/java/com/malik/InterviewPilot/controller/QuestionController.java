package com.malik.InterviewPilot.controller;

import com.malik.InterviewPilot.dto.question.QuestionPublicResponse;
import com.malik.InterviewPilot.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

/** Public browsing endpoint — answers/explanations are withheld until a test is submitted. */
@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionRepository questionRepository;

    @GetMapping
    public Page<QuestionPublicResponse> listQuestions(@RequestParam Long examId, Pageable pageable) {
        return questionRepository.findByExamId(examId, pageable).map(QuestionPublicResponse::from);
    }
}
