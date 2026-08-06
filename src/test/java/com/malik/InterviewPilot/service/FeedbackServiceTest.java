package com.malik.InterviewPilot.service;

import com.malik.InterviewPilot.dto.feedback.FeedbackRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class FeedbackServiceTest {

    private EmailService emailService;
    private FeedbackService feedbackService;

    @BeforeEach
    void setUp() {
        emailService = mock(EmailService.class);
        feedbackService = new FeedbackService(emailService);
    }

    @Test
    void submitFeedback_sendsAllFieldsThroughToTheEmailService() {
        FeedbackRequest request = new FeedbackRequest(
                "Jane Doe", "jane@example.com", "Feature Request", 4, "Please add dark mode.");

        feedbackService.submitFeedback(request);

        verify(emailService).sendFeedbackEmail("Jane Doe", "jane@example.com", "Feature Request", 4, "Please add dark mode.");
    }
}
