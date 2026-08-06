package com.malik.InterviewPilot.repository;

import com.malik.InterviewPilot.entity.TestAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestAttemptRepository extends JpaRepository<TestAttempt, Long> {
    List<TestAttempt> findTop10ByUserIdOrderByStartedAtDesc(Long userId);
    List<TestAttempt> findByStatusOrderByScoreDesc(String status);
    List<TestAttempt> findByExamId(Long examId);
}
