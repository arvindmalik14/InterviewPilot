package com.malik.InterviewPilot.controller;

import com.malik.InterviewPilot.dto.common.MessageResponse;
import com.malik.InterviewPilot.dto.test.*;
import com.malik.InterviewPilot.security.UserPrincipal;
import com.malik.InterviewPilot.service.TestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tests")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @PostMapping("/start")
    public TestAttemptResponse startTest(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody StartTestRequest request) {
        return testService.startTest(principal.getUser(), request);
    }

    @PostMapping("/{id}/submit")
    public TestResultResponse submitTest(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody SubmitTestRequest request) {
        return testService.submitTest(principal.getUser(), id, request);
    }

    @PostMapping("/{id}/stop")
    public MessageResponse stopTest(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @RequestBody StopTestRequest request) {
        testService.stopTest(principal.getUser(), id, request);
        return new MessageResponse("Test stopped");
    }

    @GetMapping("/history")
    public List<TestHistoryResponse> getHistory(@AuthenticationPrincipal UserPrincipal principal) {
        return testService.getHistory(principal.getUser());
    }
}
