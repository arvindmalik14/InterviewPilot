package com.malik.InterviewPilot.service;

import com.malik.InterviewPilot.dto.feedback.FeedbackRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedbackService {

    private final EmailService emailService;

    public void submitFeedback(FeedbackRequest request) {
        emailService.sendFeedbackEmail(request.name(), request.email(), request.category(), request.rating(), request.message());
        log.info("Feedback submitted by {}", request.email());
    }
}
