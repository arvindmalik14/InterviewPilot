package com.malik.InterviewPilot.aiqa.controller;

import com.malik.InterviewPilot.aiqa.dto.question.AiQuestionDetailResponse;
import com.malik.InterviewPilot.aiqa.dto.question.AiQuestionSummaryResponse;
import com.malik.InterviewPilot.aiqa.service.AiPlanQuestionService;
import com.malik.InterviewPilot.aiqa.service.AiQuestionBookmarkService;
import com.malik.InterviewPilot.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiQuestionController {

    private final AiPlanQuestionService planQuestionService;
    private final AiQuestionBookmarkService bookmarkService;

    @GetMapping("/questions")
    public Page<AiQuestionSummaryResponse> listQuestions(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String search,
            Pageable pageable) {
        return planQuestionService.getAccessibleQuestions(principal.getId(), categoryId, difficulty, search, pageable);
    }

    @GetMapping("/questions/category/{categoryId}")
    public Page<AiQuestionSummaryResponse> listQuestionsByCategory(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long categoryId,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String search,
            Pageable pageable) {
        return planQuestionService.getAccessibleQuestions(principal.getId(), categoryId, difficulty, search, pageable);
    }

    @GetMapping("/questions/{id}")
    public AiQuestionDetailResponse getQuestion(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        return planQuestionService.getAccessibleQuestionDetail(principal.getId(), id);
    }

    @PostMapping("/questions/{id}/bookmark")
    public ResponseEntity<Void> bookmark(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        bookmarkService.addBookmark(principal.getUser(), id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/questions/{id}/bookmark")
    public ResponseEntity<Void> removeBookmark(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        bookmarkService.removeBookmark(principal.getUser(), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/bookmarks")
    public Page<AiQuestionSummaryResponse> listBookmarks(@AuthenticationPrincipal UserPrincipal principal, Pageable pageable) {
        return bookmarkService.listBookmarks(principal.getUser(), pageable);
    }
}
