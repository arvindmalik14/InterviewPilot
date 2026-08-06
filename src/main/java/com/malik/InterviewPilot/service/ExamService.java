package com.malik.InterviewPilot.service;

import com.malik.InterviewPilot.dto.exam.ExamRequest;
import com.malik.InterviewPilot.dto.exam.ExamResponse;
import com.malik.InterviewPilot.entity.Exam;
import com.malik.InterviewPilot.exception.ResourceNotFoundException;
import com.malik.InterviewPilot.repository.ExamRepository;
import com.malik.InterviewPilot.repository.QuestionRepository;
import com.malik.InterviewPilot.repository.TestAttemptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final TestAttemptRepository testAttemptRepository;

    public List<ExamResponse> listExams() {
        return examRepository.findAll().stream()
                .map(exam -> ExamResponse.from(exam, questionRepository.findByExamId(exam.getId()).size()))
                .toList();
    }

    public ExamResponse getExam(Long id) {
        Exam exam = findExamOrThrow(id);
        return ExamResponse.from(exam, questionRepository.findByExamId(id).size());
    }

    public Exam findExamOrThrow(Long id) {
        return examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found: " + id));
    }

    public ExamResponse createExam(ExamRequest request) {
        Exam exam = Exam.builder()
                .name(request.name())
                .category(request.category())
                .description(request.description())
                .build();
        exam = examRepository.save(exam);
        return ExamResponse.from(exam, 0);
    }

    public ExamResponse updateExam(Long id, ExamRequest request) {
        Exam exam = findExamOrThrow(id);
        exam.setName(request.name());
        exam.setCategory(request.category());
        exam.setDescription(request.description());
        exam = examRepository.save(exam);
        return ExamResponse.from(exam, questionRepository.findByExamId(id).size());
    }

    @Transactional
    public void deleteExam(Long id) {
        Exam exam = findExamOrThrow(id);
        // Clear FK-dependent rows first: test attempts (cascades to their answers), then questions.
        testAttemptRepository.deleteAll(testAttemptRepository.findByExamId(id));
        questionRepository.deleteAll(questionRepository.findByExamId(id));
        examRepository.delete(exam);
    }
}
