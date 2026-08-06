package com.malik.InterviewPilot.aiqa.repository;

import com.malik.InterviewPilot.aiqa.entity.AiPlanQuestion;
import com.malik.InterviewPilot.aiqa.entity.AiQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AiPlanQuestionRepository extends JpaRepository<AiPlanQuestion, Long> {

    boolean existsByPlan_PlanIdAndQuestion_Id(Long planId, Long questionId);

    Optional<AiPlanQuestion> findByPlan_PlanIdAndQuestion_Id(Long planId, Long questionId);

    long countByPlan_PlanId(Long planId);

    void deleteByQuestion_Id(Long questionId);

    @Query("SELECT pq.question.id FROM AiPlanQuestion pq WHERE pq.plan.planId = :planId ORDER BY pq.question.id ASC")
    List<Long> findQuestionIdsByPlanIdOrderByQuestionIdAsc(@Param("planId") Long planId);

    @Query("SELECT pq.question FROM AiPlanQuestion pq WHERE pq.plan.planId = :planId ORDER BY pq.question.id ASC")
    Page<AiQuestion> findQuestionsByPlanId(@Param("planId") Long planId, Pageable pageable);
}
