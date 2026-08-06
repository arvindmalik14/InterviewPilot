package com.malik.InterviewPilot.controller;

import com.malik.InterviewPilot.dto.question.QuestionPublicResponse;
import com.malik.InterviewPilot.dto.user.UserResponse;
import com.malik.InterviewPilot.razorpay.service.PlanQuestionService;
import com.malik.InterviewPilot.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final PlanQuestionService planQuestionService;

    @GetMapping("/me")
    public UserResponse getCurrentUser(@AuthenticationPrincipal UserPrincipal principal) {
        return UserResponse.from(principal.getUser());
    }

    /** Self or admin only (IDOR guard) — questions are scoped to the user's active plan and its question_limit. */
    @GetMapping("/{userId}/questions")
    @PreAuthorize("#userId == principal.id or hasRole('ADMIN')")
    public Page<QuestionPublicResponse> getAccessibleQuestions(@PathVariable Long userId, Pageable pageable) {
        return planQuestionService.getAccessibleQuestionsForUser(userId, pageable);
    }
}
