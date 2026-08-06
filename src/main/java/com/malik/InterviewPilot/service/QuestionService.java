package com.malik.InterviewPilot.service;

import com.malik.InterviewPilot.dto.question.QuestionAdminResponse;
import com.malik.InterviewPilot.dto.question.QuestionRequest;
import com.malik.InterviewPilot.entity.Exam;
import com.malik.InterviewPilot.entity.Question;
import com.malik.InterviewPilot.exception.ResourceNotFoundException;
import com.malik.InterviewPilot.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final ExamService examService;

    public Page<QuestionAdminResponse> listByExam(Long examId, Pageable pageable) {
        return questionRepository.findByExamId(examId, pageable).map(QuestionAdminResponse::from);
    }

    public QuestionAdminResponse getQuestion(Long id) {
        return QuestionAdminResponse.from(findQuestionOrThrow(id));
    }

    public Question findQuestionOrThrow(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found: " + id));
    }

    public QuestionAdminResponse createQuestion(QuestionRequest request) {
        Exam exam = examService.findExamOrThrow(request.examId());
        Question question = toEntity(request, exam);
        return QuestionAdminResponse.from(questionRepository.save(question));
    }

    public QuestionAdminResponse updateQuestion(Long id, QuestionRequest request) {
        Question question = findQuestionOrThrow(id);
        Exam exam = examService.findExamOrThrow(request.examId());

        question.setExam(exam);
        question.setQuestion(request.question());
        question.setOptionA(request.optionA());
        question.setOptionB(request.optionB());
        question.setOptionC(request.optionC());
        question.setOptionD(request.optionD());
        question.setAnswer(request.answer());
        question.setExplanation(request.explanation());
        if (request.difficulty() != null) {
            question.setDifficulty(request.difficulty());
        }

        return QuestionAdminResponse.from(questionRepository.save(question));
    }

    public void deleteQuestion(Long id) {
        Question question = findQuestionOrThrow(id);
        questionRepository.delete(question);
    }

    private Question toEntity(QuestionRequest request, Exam exam) {
        return Question.builder()
                .exam(exam)
                .question(request.question())
                .optionA(request.optionA())
                .optionB(request.optionB())
                .optionC(request.optionC())
                .optionD(request.optionD())
                .answer(request.answer())
                .explanation(request.explanation())
                .difficulty(request.difficulty() != null ? request.difficulty() : "MEDIUM")
                .build();
    }
}
