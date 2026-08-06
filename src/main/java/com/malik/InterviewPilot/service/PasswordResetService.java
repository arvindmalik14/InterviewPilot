package com.malik.InterviewPilot.service;

import com.malik.InterviewPilot.dto.auth.AuthResponse;
import com.malik.InterviewPilot.dto.auth.ResetPasswordRequest;
import com.malik.InterviewPilot.dto.user.UserResponse;
import com.malik.InterviewPilot.entity.User;
import com.malik.InterviewPilot.exception.BadRequestException;
import com.malik.InterviewPilot.exception.ResourceNotFoundException;
import com.malik.InterviewPilot.repository.UserRepository;
import com.malik.InterviewPilot.security.JwtService;
import com.malik.InterviewPilot.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

/** Forgot-password: issues and validates a time-boxed temporary password; login handles the temp-password check. */
@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TemporaryPasswordGenerator temporaryPasswordGenerator;
    private final EmailService emailService;
    private final JwtService jwtService;

    @Value("${app.security.temporary-password-ttl-hours:24}")
    private long temporaryPasswordTtlHours;

    /** Throttles re-issuance so repeatedly calling this endpoint can't be used to spam a victim's inbox. */
    @Value("${app.security.temporary-password-resend-cooldown-seconds:60}")
    private long resendCooldownSeconds;

    /**
     * Always succeeds from the caller's point of view, whether or not the email is registered —
     * this avoids leaking account existence (email enumeration) to an unauthenticated caller.
     */
    @Transactional
    public void requestPasswordReset(String email) {
        userRepository.findByEmail(email).ifPresentOrElse(
                user -> issueTemporaryPasswordWithCooldown(user, emailService::sendTemporaryPasswordEmail,
                        "Forgot-password re-requested too soon for user id={}, ignoring"),
                () -> log.info("Forgot-password requested for an email with no matching account"));
    }

    /**
     * Issues the very first temporary password for a brand-new signup — no cooldown check, since a
     * freshly created account can't already have one pending (see AuthService.signup).
     */
    @Transactional
    public void issueSignupTemporaryPassword(User user) {
        issueTemporaryPassword(user, emailService::sendWelcomeEmail);
    }

    /**
     * "I lost/never received my temporary password" — same cooldown-guarded re-issuance as
     * forgot-password, but using the welcome template since this is part of the signup journey.
     */
    @Transactional
    public void resendTemporaryPassword(String email) {
        userRepository.findByEmail(email).ifPresentOrElse(
                user -> issueTemporaryPasswordWithCooldown(user, emailService::sendWelcomeEmail,
                        "Resend-password re-requested too soon for user id={}, ignoring"),
                () -> log.info("Resend-password requested for an email with no matching account"));
    }

    private void issueTemporaryPasswordWithCooldown(User user, TemporaryPasswordSender sender, String cooldownLogMessage) {
        if (isWithinResendCooldown(user)) {
            log.info(cooldownLogMessage, user.getId());
            return;
        }
        issueTemporaryPassword(user, sender);
    }

    private void issueTemporaryPassword(User user, TemporaryPasswordSender sender) {
        String temporaryPassword = temporaryPasswordGenerator.generate();
        user.setTemporaryPasswordHash(passwordEncoder.encode(temporaryPassword));
        user.setTemporaryPasswordExpiry(Instant.now().plus(Duration.ofHours(temporaryPasswordTtlHours)));
        user.setPasswordResetRequired(true);
        userRepository.save(user);
        log.info("Temporary password issued for user id={}", user.getId());

        try {
            sender.send(user.getEmail(), user.getName(), temporaryPassword);
        } catch (Exception ex) {
            log.error("Failed to send temporary password email for user id={}", user.getId(), ex);
        }
    }

    @FunctionalInterface
    private interface TemporaryPasswordSender {
        void send(String toEmail, String recipientName, String temporaryPassword);
    }

    private boolean isWithinResendCooldown(User user) {
        if (user.getTemporaryPasswordExpiry() == null) {
            return false;
        }
        Instant issuedAt = user.getTemporaryPasswordExpiry().minus(Duration.ofHours(temporaryPasswordTtlHours));
        return issuedAt.plusSeconds(resendCooldownSeconds).isAfter(Instant.now());
    }

    /**
     * Only callable by the account holder themselves (enforced via the authenticated principal, not
     * the request body's email) and only while a temporary-password reset is actually pending —
     * otherwise this would let anyone who guesses/knows an email take over the account.
     */
    @Transactional
    public AuthResponse resetPassword(User authenticatedUser, ResetPasswordRequest request) {
        if (!authenticatedUser.getEmail().equalsIgnoreCase(request.email())) {
            throw new AccessDeniedException("You may only reset your own password");
        }
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new BadRequestException("New password and confirm password do not match");
        }

        User user = userRepository.findById(authenticatedUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + authenticatedUser.getId()));

        if (!user.isPasswordResetRequired()) {
            throw new BadRequestException("No password reset is pending for this account");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setTemporaryPasswordHash(null);
        user.setTemporaryPasswordExpiry(null);
        user.setPasswordResetRequired(false);
        user.setFailedLoginAttempts(0);
        user.setAccountLockedUntil(null);
        // Any JWT issued before this instant is now rejected — this is how "invalidate all active
        // sessions" is enforced for a stateless-JWT app (no server-side session store to clear).
        user.setPasswordChangedAt(Instant.now());
        userRepository.save(user);
        log.info("Password reset completed for user id={}", user.getId());

        String token = jwtService.generateToken(new UserPrincipal(user));
        return new AuthResponse(token, UserResponse.from(user), false);
    }
}
