package com.malik.InterviewPilot.service;

import com.malik.InterviewPilot.dto.ai.*;
import com.malik.InterviewPilot.entity.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Stubbed AI layer: returns canned/derived responses instead of calling a real
 * LLM provider, so the rest of the product can be built and demoed without an
 * API key. Each method is the single seam to swap in a real OpenAI call later —
 * the request/response DTOs are already shaped for that.
 */
@Service
@RequiredArgsConstructor
public class AiExplanationService {

    private final QuestionService questionService;

    public AiExplainResponse explainQuestion(AiExplainRequest request) {
        Question question = questionService.findQuestionOrThrow(request.questionId());

        String base = question.getExplanation() != null && !question.getExplanation().isBlank()
                ? question.getExplanation()
                : "This question tests core knowledge of the topic covered by \"" + question.getQuestion() + "\".";

        String explanation = base + " The correct answer is option " + question.getAnswer().toUpperCase()
                + ". (AI explanation is stubbed in this build — connect an OpenAI API key to generate live, "
                + "context-aware explanations here.)";

        return new AiExplainResponse(question.getId(), explanation);
    }

    public GenerateQuestionsResponse generateQuestions(GenerateQuestionsRequest request) {
        List<GeneratedQuestion> questions = java.util.stream.IntStream.rangeClosed(1, request.count())
                .mapToObj(i -> new GeneratedQuestion(
                        "[Sample #" + i + "] Which concept in " + request.technology()
                                + " is most relevant for a " + request.experienceLevel() + " engineer?",
                        "Option A", "Option B", "Option C", "Option D",
                        "A",
                        "Stubbed explanation — wire up an OpenAI API key to generate real, tailored questions.",
                        "MEDIUM"))
                .toList();

        return new GenerateQuestionsResponse(request.technology(), request.experienceLevel(), questions);
    }

    public ResumeAnalysisResponse analyzeResume(ResumeAnalysisRequest request) {
        int wordCount = request.resumeText().trim().split("\\s+").length;
        int score = Math.min(95, 50 + Math.min(wordCount / 10, 40));

        return new ResumeAnalysisResponse(
                score,
                List.of("Resume received (" + wordCount + " words) and parsed successfully."),
                List.of(
                        "AI resume analysis is stubbed in this build.",
                        "Connect an OpenAI API key to get real, tailored improvement suggestions here."));
    }

    public CodeReviewResponse reviewCode(CodeReviewRequest request) {
        int lineCount = request.code().split("\n").length;

        return new CodeReviewResponse(
                List.of(
                        "Received " + lineCount + " lines of "
                                + (request.language() != null ? request.language() : "code") + " for review.",
                        "AI code review is stubbed in this build — connect an OpenAI API key for real optimization suggestions."),
                "Stubbed review: no real static analysis or AI model was invoked.");
    }
}
