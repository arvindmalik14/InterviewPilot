package com.malik.InterviewPilot.service;

import com.malik.InterviewPilot.dto.auth.AuthResponse;
import com.malik.InterviewPilot.dto.auth.ResetPasswordRequest;
import com.malik.InterviewPilot.entity.User;
import com.malik.InterviewPilot.exception.BadRequestException;
import com.malik.InterviewPilot.repository.UserRepository;
import com.malik.InterviewPilot.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PasswordResetServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private TemporaryPasswordGenerator temporaryPasswordGenerator;
    private EmailService emailService;
    private JwtService jwtService;
    private PasswordResetService passwordResetService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        temporaryPasswordGenerator = mock(TemporaryPasswordGenerator.class);
        emailService = mock(EmailService.class);
        jwtService = mock(JwtService.class);
        passwordResetService = new PasswordResetService(
                userRepository, passwordEncoder, temporaryPasswordGenerator, emailService, jwtService);
        ReflectionTestUtils.setField(passwordResetService, "temporaryPasswordTtlHours", 24L);
        ReflectionTestUtils.setField(passwordResetService, "resendCooldownSeconds", 60L);
    }

    private User existingUser() {
        return User.builder().id(1L).name("Arvind Kumar").email("arvind@example.com")
                .password("old-hash").passwordResetRequired(true).build();
    }

    @Test
    void requestPasswordReset_issuesAndEmailsATemporaryPassword_whenAccountExists() {
        User user = existingUser();
        user.setPasswordResetRequired(false);
        when(userRepository.findByEmail("arvind@example.com")).thenReturn(Optional.of(user));
        when(temporaryPasswordGenerator.generate()).thenReturn("IVE09lXSNRmF");
        when(passwordEncoder.encode("IVE09lXSNRmF")).thenReturn("bcrypt-hash");

        passwordResetService.requestPasswordReset("arvind@example.com");

        assertEquals("bcrypt-hash", user.getTemporaryPasswordHash());
        assertTrue(user.isPasswordResetRequired());
        assertTrue(user.getTemporaryPasswordExpiry().isAfter(Instant.now().plus(23, ChronoUnit.HOURS)));
        verify(emailService).sendTemporaryPasswordEmail("arvind@example.com", "Arvind Kumar", "IVE09lXSNRmF");
        verify(userRepository).save(user);
    }

    @Test
    void requestPasswordReset_doesNothing_whenNoAccountMatchesTheEmail() {
        when(userRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

        passwordResetService.requestPasswordReset("nobody@example.com");

        verify(userRepository, never()).save(any());
        verify(emailService, never()).sendTemporaryPasswordEmail(anyString(), anyString(), anyString());
    }

    @Test
    void requestPasswordReset_skipsReissuing_whenCalledAgainWithinTheCooldownWindow() {
        User user = existingUser();
        user.setTemporaryPasswordExpiry(Instant.now().plus(24, ChronoUnit.HOURS).minusSeconds(30)); // issued ~30s ago
        when(userRepository.findByEmail("arvind@example.com")).thenReturn(Optional.of(user));

        passwordResetService.requestPasswordReset("arvind@example.com");

        verify(userRepository, never()).save(any());
        verify(emailService, never()).sendTemporaryPasswordEmail(anyString(), anyString(), anyString());
    }

    @Test
    void requestPasswordReset_stillSucceeds_whenTheEmailProviderThrows() {
        User user = existingUser();
        when(userRepository.findByEmail("arvind@example.com")).thenReturn(Optional.of(user));
        when(temporaryPasswordGenerator.generate()).thenReturn("IVE09lXSNRmF");
        org.mockito.Mockito.doThrow(new RuntimeException("SMTP down"))
                .when(emailService).sendTemporaryPasswordEmail(anyString(), anyString(), anyString());

        passwordResetService.requestPasswordReset("arvind@example.com"); // must not throw

        verify(userRepository).save(user);
    }

    @Test
    void resetPassword_succeeds_clearsTemporaryPasswordState_andReturnsAFreshToken() {
        User user = existingUser();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("NewPass@123")).thenReturn("new-hash");
        when(jwtService.generateToken(any())).thenReturn("fresh-jwt");

        ResetPasswordRequest request = new ResetPasswordRequest("arvind@example.com", "NewPass@123", "NewPass@123");
        AuthResponse response = passwordResetService.resetPassword(user, request);

        assertEquals("fresh-jwt", response.token());
        assertFalse(response.requiresPasswordReset());
        assertEquals("new-hash", user.getPassword());
        assertNull(user.getTemporaryPasswordHash());
        assertNull(user.getTemporaryPasswordExpiry());
        assertFalse(user.isPasswordResetRequired());
        verify(userRepository).save(user);
    }

    @Test
    void resetPassword_rejects_whenConfirmPasswordDoesNotMatch() {
        User user = existingUser();
        ResetPasswordRequest request = new ResetPasswordRequest("arvind@example.com", "NewPass@123", "Different@123");

        assertThrows(BadRequestException.class, () -> passwordResetService.resetPassword(user, request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void resetPassword_rejects_whenNoResetIsPending() {
        User user = existingUser();
        user.setPasswordResetRequired(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        ResetPasswordRequest request = new ResetPasswordRequest("arvind@example.com", "NewPass@123", "NewPass@123");

        assertThrows(BadRequestException.class, () -> passwordResetService.resetPassword(user, request));
    }

    @Test
    void resetPassword_rejects_whenRequestEmailDoesNotMatchTheAuthenticatedUser() {
        User user = existingUser();
        ResetPasswordRequest request = new ResetPasswordRequest("someone-else@example.com", "NewPass@123", "NewPass@123");

        assertThrows(AccessDeniedException.class, () -> passwordResetService.resetPassword(user, request));
        verify(userRepository, never()).findById(any());
    }

    @Test
    void issueSignupTemporaryPassword_usesTheWelcomeTemplate_withNoCooldownCheck() {
        User user = User.builder().id(9L).name("Arvind Kumar").email("arvind@example.com").build();
        when(temporaryPasswordGenerator.generate()).thenReturn("AbC123@xyz");
        when(passwordEncoder.encode("AbC123@xyz")).thenReturn("bcrypt-hash");

        passwordResetService.issueSignupTemporaryPassword(user);

        assertEquals("bcrypt-hash", user.getTemporaryPasswordHash());
        assertTrue(user.isPasswordResetRequired());
        verify(emailService).sendWelcomeEmail("arvind@example.com", "Arvind Kumar", "AbC123@xyz");
        verify(emailService, never()).sendTemporaryPasswordEmail(anyString(), anyString(), anyString());
        verify(userRepository).save(user);
    }

    @Test
    void resendTemporaryPassword_usesTheWelcomeTemplate_andRespectsTheCooldown() {
        User user = existingUser();
        user.setTemporaryPasswordExpiry(Instant.now().plus(24, ChronoUnit.HOURS).minusSeconds(30)); // issued ~30s ago
        when(userRepository.findByEmail("arvind@example.com")).thenReturn(Optional.of(user));

        passwordResetService.resendTemporaryPassword("arvind@example.com");

        verify(userRepository, never()).save(any());
        verify(emailService, never()).sendWelcomeEmail(anyString(), anyString(), anyString());
    }

    @Test
    void resendTemporaryPassword_doesNothing_whenNoAccountMatchesTheEmail() {
        when(userRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

        passwordResetService.resendTemporaryPassword("nobody@example.com");

        verify(userRepository, never()).save(any());
        verify(emailService, never()).sendWelcomeEmail(anyString(), anyString(), anyString());
    }
}
