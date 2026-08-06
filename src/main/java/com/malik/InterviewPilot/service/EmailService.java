package com.malik.InterviewPilot.service;

public interface EmailService {
    void sendTemporaryPasswordEmail(String toEmail, String recipientName, String temporaryPassword);

    void sendWelcomeEmail(String toEmail, String recipientName, String temporaryPassword);

    void sendFeedbackEmail(String name, String email, String category, Integer rating, String message);
}
