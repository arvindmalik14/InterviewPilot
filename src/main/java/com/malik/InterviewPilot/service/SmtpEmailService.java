package com.malik.InterviewPilot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmtpEmailService implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromAddress;

    @Value("${app.mail.support-address}")
    private String supportAddress;

    @Value("${app.frontend.login-url}")
    private String loginUrl;

    @Override
    public void sendTemporaryPasswordEmail(String toEmail, String recipientName, String temporaryPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(toEmail);
        message.setSubject("Password Reset Request – InterviewPilot Platform: Your New Default Password");
        message.setText(buildBody(recipientName, toEmail, temporaryPassword));
        mailSender.send(message);
    }

    private String buildBody(String recipientName, String email, String temporaryPassword) {
        return """
                Dear %s,

                We're here to help you recover your login credentials.

                To reset your password and access the InterviewPilot Platform, click on the URL below and enter the following credentials.

                Portal URL:
                %s

                Username:
                %s

                Temporary Password:
                %s

                After logging in, you will be prompted to create a new password.

                If you have any questions or need assistance, please contact us at:

                %s

                Warm regards,

                Admin
                InterviewPilot Platform
                """.formatted(recipientName, loginUrl, email, temporaryPassword, supportAddress);
    }

    @Override
    public void sendWelcomeEmail(String toEmail, String recipientName, String temporaryPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(toEmail);
        message.setSubject("Welcome to InterviewPilot – Your Temporary Password");
        message.setText(buildWelcomeBody(recipientName, toEmail, temporaryPassword));
        mailSender.send(message);
    }

    private String buildWelcomeBody(String recipientName, String email, String temporaryPassword) {
        return """
                Dear %s,

                Welcome to InterviewPilot.

                Your account has been created successfully.

                Please use the following credentials to access the platform.

                Portal URL:

                %s

                Username:

                %s

                Temporary Password:

                %s

                For security reasons, you will be asked to create a new password after your first login.

                If you have any questions, please contact us.

                Warm regards,

                Admin

                InterviewPilot Platform
                """.formatted(recipientName, loginUrl, email, temporaryPassword);
    }
}
