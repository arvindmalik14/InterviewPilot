package com.malik.InterviewPilot.controller;

import com.malik.InterviewPilot.dto.ai.*;
import com.malik.InterviewPilot.service.AiExplanationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiExplanationService aiExplanationService;

    @PostMapping("/explain")
    public AiExplainResponse explain(@Valid @RequestBody AiExplainRequest request) {
        return aiExplanationService.explainQuestion(request);
    }

    @PostMapping("/generate-questions")
    public GenerateQuestionsResponse generateQuestions(@Valid @RequestBody GenerateQuestionsRequest request) {
        return aiExplanationService.generateQuestions(request);
    }

    @PostMapping("/resume-analysis")
    public ResumeAnalysisResponse analyzeResume(@Valid @RequestBody ResumeAnalysisRequest request) {
        return aiExplanationService.analyzeResume(request);
    }

    @PostMapping("/code-review")
    public CodeReviewResponse reviewCode(@Valid @RequestBody CodeReviewRequest request) {
        return aiExplanationService.reviewCode(request);
    }
}
