package com.malik.InterviewPilot.aiqa.repository;

import com.malik.InterviewPilot.aiqa.entity.AiContentStatus;
import com.malik.InterviewPilot.aiqa.entity.AiQuestionCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AiQuestionCategoryRepository extends JpaRepository<AiQuestionCategory, Long> {

    List<AiQuestionCategory> findByQuestion_Id(Long questionId);

    List<AiQuestionCategory> findByQuestion_IdIn(List<Long> questionIds);

    long countByCategory_IdAndQuestion_Status(Long categoryId, AiContentStatus status);

    void deleteByQuestion_Id(Long questionId);

    void deleteByCategory_Id(Long categoryId);
}
