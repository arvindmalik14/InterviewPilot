package com.malik.InterviewPilot.aiqa.service;

import com.malik.InterviewPilot.aiqa.dto.category.AiCategoryRequest;
import com.malik.InterviewPilot.aiqa.dto.category.AiCategoryResponse;
import com.malik.InterviewPilot.aiqa.entity.AiCategory;
import com.malik.InterviewPilot.aiqa.entity.AiContentStatus;
import com.malik.InterviewPilot.aiqa.repository.AiCategoryRepository;
import com.malik.InterviewPilot.aiqa.repository.AiQuestionCategoryRepository;
import com.malik.InterviewPilot.exception.DuplicateResourceException;
import com.malik.InterviewPilot.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AiCategoryService {

    private final AiCategoryRepository categoryRepository;
    private final AiQuestionCategoryRepository questionCategoryRepository;

    @Transactional(readOnly = true)
    public List<AiCategoryResponse> listActiveCategories() {
        return categoryRepository.findByStatus(AiContentStatus.ACTIVE).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AiCategoryResponse> listAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public AiCategoryResponse createCategory(AiCategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.name())) {
            throw new DuplicateResourceException("A category named '" + request.name() + "' already exists");
        }
        AiCategory category = AiCategory.builder()
                .name(request.name())
                .description(request.description())
                .status(request.status() != null ? request.status() : AiContentStatus.ACTIVE)
                .build();
        return toResponse(categoryRepository.save(category));
    }

    public AiCategoryResponse updateCategory(Long categoryId, AiCategoryRequest request) {
        AiCategory category = findCategoryOrThrow(categoryId);
        if (!category.getName().equalsIgnoreCase(request.name()) && categoryRepository.existsByNameIgnoreCase(request.name())) {
            throw new DuplicateResourceException("A category named '" + request.name() + "' already exists");
        }
        category.setName(request.name());
        category.setDescription(request.description());
        if (request.status() != null) {
            category.setStatus(request.status());
        }
        return toResponse(categoryRepository.save(category));
    }

    public void deleteCategory(Long categoryId) {
        AiCategory category = findCategoryOrThrow(categoryId);
        questionCategoryRepository.deleteByCategory_Id(categoryId);
        categoryRepository.delete(category);
    }

    @Transactional(readOnly = true)
    public AiCategory findCategoryOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("AI category not found: " + categoryId));
    }

    private AiCategoryResponse toResponse(AiCategory category) {
        long questionCount = questionCategoryRepository.countByCategory_IdAndQuestion_Status(category.getId(), AiContentStatus.ACTIVE);
        return AiCategoryResponse.from(category, questionCount);
    }
}
