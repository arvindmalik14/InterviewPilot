package com.malik.InterviewPilot.razorpay.repository;

import com.malik.InterviewPilot.entity.Question;
import com.malik.InterviewPilot.razorpay.entity.PlanQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PlanQuestionRepository extends JpaRepository<PlanQuestion, Long> {

    boolean existsByPlan_PlanIdAndQuestion_Id(Long planId, Long questionId);

    Optional<PlanQuestion> findByPlan_PlanIdAndQuestion_Id(Long planId, Long questionId);

    long countByPlan_PlanId(Long planId);

    /** Ordered by question id so callers can reliably cap/paginate across a stable sequence. */
    @Query("select pq.question from PlanQuestion pq where pq.plan.planId = :planId order by pq.question.id asc")
    Page<Question> findQuestionsByPlanId(@Param("planId") Long planId, Pageable pageable);
}
