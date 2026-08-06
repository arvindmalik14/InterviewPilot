package com.malik.InterviewPilot.aiqa.controller;

import com.malik.InterviewPilot.aiqa.dto.category.AiCategoryResponse;
import com.malik.InterviewPilot.aiqa.service.AiCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ai/categories")
@RequiredArgsConstructor
public class AiCategoryController {

    private final AiCategoryService categoryService;

    @GetMapping
    public List<AiCategoryResponse> listCategories() {
        return categoryService.listActiveCategories();
    }
}
