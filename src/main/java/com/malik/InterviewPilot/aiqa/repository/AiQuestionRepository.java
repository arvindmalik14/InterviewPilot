package com.malik.InterviewPilot.aiqa.repository;

import com.malik.InterviewPilot.aiqa.entity.AiContentStatus;
import com.malik.InterviewPilot.aiqa.entity.AiQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AiQuestionRepository extends JpaRepository<AiQuestion, Long> {

    Page<AiQuestion> findByStatus(AiContentStatus status, Pageable pageable);

    /**
     * Filters within a caller-supplied set of question ids (the user's plan-accessible set —
     * see AiPlanQuestionService) by status/category/difficulty/title-search, all optional except
     * status. DISTINCT is required because the LEFT JOIN fans out one row per assigned category.
     */
    @Query("""
            SELECT DISTINCT q FROM AiQuestion q
            LEFT JOIN AiQuestionCategory qc ON qc.question = q
            WHERE q.id IN :accessibleIds
              AND q.status = :status
              AND (:categoryId IS NULL OR qc.category.id = :categoryId)
              AND (:difficulty IS NULL OR q.difficultyLevel = :difficulty)
              AND (:search IS NULL OR LOWER(q.title) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<AiQuestion> search(
            @Param("accessibleIds") List<Long> accessibleIds,
            @Param("status") AiContentStatus status,
            @Param("categoryId") Long categoryId,
            @Param("difficulty") String difficulty,
            @Param("search") String search,
            Pageable pageable);
}
