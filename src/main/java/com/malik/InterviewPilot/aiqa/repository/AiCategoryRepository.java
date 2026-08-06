package com.malik.InterviewPilot.aiqa.repository;

import com.malik.InterviewPilot.aiqa.entity.AiCategory;
import com.malik.InterviewPilot.aiqa.entity.AiContentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AiCategoryRepository extends JpaRepository<AiCategory, Long> {
    List<AiCategory> findByStatus(AiContentStatus status);

    boolean existsByNameIgnoreCase(String name);

    Optional<AiCategory> findByNameIgnoreCase(String name);
}
