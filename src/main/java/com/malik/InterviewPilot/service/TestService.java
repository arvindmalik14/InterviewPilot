package com.malik.InterviewPilot.service;

import com.malik.InterviewPilot.dto.question.QuestionPublicResponse;
import com.malik.InterviewPilot.dto.test.*;
import com.malik.InterviewPilot.entity.*;
import com.malik.InterviewPilot.exception.BadRequestException;
import com.malik.InterviewPilot.exception.ResourceNotFoundException;
import com.malik.InterviewPilot.repository.QuestionRepository;
import com.malik.InterviewPilot.repository.TestAttemptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TestService {

    private static final int DEFAULT_QUESTION_COUNT = 10;

    private final TestAttemptRepository testAttemptRepository;
    private final QuestionRepository questionRepository;
    private final ExamService examService;

    public TestAttemptResponse startTest(User user, StartTestRequest request) {
        Exam exam = examService.findExamOrThrow(request.examId());

        List<Question> pool = new ArrayList<>(questionRepository.findByExamId(exam.getId()));
        if (pool.isEmpty()) {
            throw new BadRequestException("This exam has no questions yet");
        }

        int requested = request.questionCount() != null ? request.questionCount() : DEFAULT_QUESTION_COUNT;
        int count = Math.min(requested, pool.size());

        Collections.shuffle(pool);
        List<Question> selected = pool.subList(0, count);

        TestAttempt attempt = TestAttempt.builder()
                .user(user)
                .exam(exam)
                .totalQuestions(selected.size())
                .status("IN_PROGRESS")
                .build();
        attempt = testAttemptRepository.save(attempt);

        List<QuestionPublicResponse> questions = selected.stream().map(QuestionPublicResponse::from).toList();
        return TestAttemptResponse.from(attempt, questions);
    }

    public TestResultResponse submitTest(User user, Long testAttemptId, SubmitTestRequest request) {
        TestAttempt attempt = findAttemptOrThrow(testAttemptId);

        if (!attempt.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("This test attempt does not belong to you");
        }
        if (!"IN_PROGRESS".equals(attempt.getStatus())) {
            throw new BadRequestException("This test has already been submitted");
        }

        List<AnswerResultResponse> results = new ArrayList<>();
        int correctCount = 0;

        for (SubmitAnswerRequest submitted : request.answers()) {
            Question question = questionRepository.findById(submitted.questionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Question not found: " + submitted.questionId()));

            boolean isCorrect = question.getAnswer().equalsIgnoreCase(submitted.selectedOption());
            if (isCorrect) {
                correctCount++;
            }

            TestAnswer answer = TestAnswer.builder()
                    .testAttempt(attempt)
                    .question(question)
                    .selectedOption(submitted.selectedOption())
                    .correct(isCorrect)
                    .build();
            attempt.getAnswers().add(answer);

            results.add(new AnswerResultResponse(
                    question.getId(), question.getQuestion(),
                    question.getOptionA(), question.getOptionB(), question.getOptionC(), question.getOptionD(),
                    submitted.selectedOption(), question.getAnswer(), isCorrect, question.getExplanation()));
        }

        int score = Math.round((correctCount * 100f) / attempt.getTotalQuestions());
        attempt.setScore(score);
        attempt.setDurationSeconds(request.durationSeconds());
        attempt.setStatus("COMPLETED");
        attempt.setCompletedAt(java.time.Instant.now());
        testAttemptRepository.save(attempt);

        return new TestResultResponse(
                attempt.getId(), attempt.getExam().getName(), score,
                attempt.getTotalQuestions(), correctCount, request.durationSeconds(), results);
    }

    public List<TestHistoryResponse> getHistory(User user) {
        return testAttemptRepository.findByUserIdOrderByStartedAtDesc(user.getId()).stream()
                .map(TestHistoryResponse::from)
                .toList();
    }

    public TestAttempt findAttemptOrThrow(Long id) {
        return testAttemptRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Test attempt not found: " + id));
    }
}
