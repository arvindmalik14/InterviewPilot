package com.malik.InterviewPilot.controller;

import com.malik.InterviewPilot.dto.exam.ExamResponse;
import com.malik.InterviewPilot.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @GetMapping
    public List<ExamResponse> listExams() {
        return examService.listExams();
    }

    @GetMapping("/{id}")
    public ExamResponse getExam(@PathVariable Long id) {
        return examService.getExam(id);
    }
}
