package com.malik.InterviewPilot.aiqa.repository;

import com.malik.InterviewPilot.aiqa.entity.AiQuestionBookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface AiQuestionBookmarkRepository extends JpaRepository<AiQuestionBookmark, Long> {

    boolean existsByUser_IdAndQuestion_Id(Long userId, Long questionId);

    Optional<AiQuestionBookmark> findByUser_IdAndQuestion_Id(Long userId, Long questionId);

    Page<AiQuestionBookmark> findByUser_Id(Long userId, Pageable pageable);

    void deleteByQuestion_Id(Long questionId);

    @Query("SELECT b.question.id FROM AiQuestionBookmark b WHERE b.user.id = :userId AND b.question.id IN :questionIds")
    Set<Long> findBookmarkedQuestionIds(@Param("userId") Long userId, @Param("questionIds") List<Long> questionIds);
}
