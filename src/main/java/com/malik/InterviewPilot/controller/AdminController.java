package com.malik.InterviewPilot.controller;

import com.malik.InterviewPilot.dto.exam.ExamRequest;
import com.malik.InterviewPilot.dto.exam.ExamResponse;
import com.malik.InterviewPilot.dto.question.QuestionAdminResponse;
import com.malik.InterviewPilot.dto.question.QuestionRequest;
import com.malik.InterviewPilot.service.ExamService;
import com.malik.InterviewPilot.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Admin-only CRUD, gated by SecurityConfig's "/api/admin/**" -> ROLE_ADMIN rule. */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ExamService examService;
    private final QuestionService questionService;

    @PostMapping("/exams")
    public ResponseEntity<ExamResponse> createExam(@Valid @RequestBody ExamRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(examService.createExam(request));
    }

    @PutMapping("/exams/{id}")
    public ExamResponse updateExam(@PathVariable Long id, @Valid @RequestBody ExamRequest request) {
        return examService.updateExam(id, request);
    }

    @DeleteMapping("/exams/{id}")
    public ResponseEntity<Void> deleteExam(@PathVariable Long id) {
        examService.deleteExam(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/questions")
    public Page<QuestionAdminResponse> listQuestions(@RequestParam Long examId, Pageable pageable) {
        return questionService.listByExam(examId, pageable);
    }

    @GetMapping("/questions/{id}")
    public QuestionAdminResponse getQuestion(@PathVariable Long id) {
        return questionService.getQuestion(id);
    }

    @PostMapping("/questions")
    public ResponseEntity<QuestionAdminResponse> createQuestion(@Valid @RequestBody QuestionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(questionService.createQuestion(request));
    }

    @PutMapping("/questions/{id}")
    public QuestionAdminResponse updateQuestion(@PathVariable Long id, @Valid @RequestBody QuestionRequest request) {
        return questionService.updateQuestion(id, request);
    }

    @DeleteMapping("/questions/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }
}
