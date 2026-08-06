package com.malik.InterviewPilot.repository;

import com.malik.InterviewPilot.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    Page<Question> findByExamId(Long examId, Pageable pageable);
    List<Question> findByExamId(Long examId);
}
