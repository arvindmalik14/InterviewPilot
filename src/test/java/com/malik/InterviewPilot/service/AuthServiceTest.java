package com.malik.InterviewPilot.service;

import com.malik.InterviewPilot.dto.auth.AuthResponse;
import com.malik.InterviewPilot.dto.auth.LoginRequest;
import com.malik.InterviewPilot.dto.auth.SignupRequest;
import com.malik.InterviewPilot.dto.auth.SignupResponse;
import com.malik.InterviewPilot.entity.User;
import com.malik.InterviewPilot.exception.DuplicateResourceException;
import com.malik.InterviewPilot.razorpay.service.SubscriptionService;
import com.malik.InterviewPilot.repository.UserRepository;
import com.malik.InterviewPilot.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;
    private SubscriptionService subscriptionService;
    private PasswordResetService passwordResetService;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        authenticationManager = mock(AuthenticationManager.class);
        jwtService = mock(JwtService.class);
        subscriptionService = mock(SubscriptionService.class);
        passwordResetService = mock(PasswordResetService.class);
        authService = new AuthService(
                userRepository, passwordEncoder, authenticationManager, jwtService, subscriptionService,
                passwordResetService);
        ReflectionTestUtils.setField(authService, "maxFailedLoginAttempts", 3);
        ReflectionTestUtils.setField(authService, "accountLockoutMinutes", 15L);
    }

    private User activeUser() {
        return User.builder().id(1L).email("user@example.com").password("hashed").failedLoginAttempts(0).build();
    }

    @Test
    void login_succeedsWithPrimaryPassword_andClearsAnyPriorFailedAttempts() {
        User user = activeUser();
        user.setFailedLoginAttempts(2);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any())).thenReturn("jwt-token");

        AuthResponse response = authService.login(new LoginRequest("user@example.com", "correct-password"));

        assertEquals("jwt-token", response.token());
        assertFalse(response.requiresPasswordReset());
        assertEquals(0, user.getFailedLoginAttempts());
        verify(userRepository).save(user);
    }

    @Test
    void login_fallsBackToTemporaryPassword_whenPrimaryPasswordFails() {
        User user = activeUser();
        user.setFailedLoginAttempts(1);
        user.setTemporaryPasswordHash("bcrypt-hash-of-temp-password");
        user.setTemporaryPasswordExpiry(Instant.now().plus(1, ChronoUnit.HOURS));

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        doThrow(new BadCredentialsException("bad creds")).when(authenticationManager).authenticate(any());
        when(passwordEncoder.matches("IVE09lXSNRmF", "bcrypt-hash-of-temp-password")).thenReturn(true);
        when(jwtService.generateToken(any())).thenReturn("jwt-token");

        AuthResponse response = authService.login(new LoginRequest("user@example.com", "IVE09lXSNRmF"));

        assertTrue(response.requiresPasswordReset());
        verify(userRepository).save(user);
    }

    @Test
    void login_rejectsExpiredTemporaryPassword_andCountsItAsAFailedAttempt() {
        User user = activeUser();
        user.setTemporaryPasswordHash("bcrypt-hash-of-temp-password");
        user.setTemporaryPasswordExpiry(Instant.now().minus(1, ChronoUnit.HOURS));

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        doThrow(new BadCredentialsException("bad creds")).when(authenticationManager).authenticate(any());

        assertThrows(BadCredentialsException.class,
                () -> authService.login(new LoginRequest("user@example.com", "IVE09lXSNRmF")));

        assertEquals(1, user.getFailedLoginAttempts());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void login_locksTheAccount_afterReachingTheMaxFailedAttempts() {
        User user = activeUser();
        user.setFailedLoginAttempts(2); // one more failure reaches the threshold of 3
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        doThrow(new BadCredentialsException("bad creds")).when(authenticationManager).authenticate(any());

        assertThrows(BadCredentialsException.class,
                () -> authService.login(new LoginRequest("user@example.com", "wrong-password")));

        assertEquals(3, user.getFailedLoginAttempts());
        assertTrue(user.getAccountLockedUntil().isAfter(Instant.now()));
    }

    @Test
    void login_propagatesLockedException_withoutTouchingFailedAttemptCounters() {
        User user = activeUser();
        user.setFailedLoginAttempts(1);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        doThrow(new LockedException("locked")).when(authenticationManager).authenticate(any());

        assertThrows(LockedException.class,
                () -> authService.login(new LoginRequest("user@example.com", "whatever")));

        assertEquals(1, user.getFailedLoginAttempts());
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_doesNotThrowNpe_whenEmailDoesNotMatchAnyAccount() {
        when(userRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());
        doThrow(new BadCredentialsException("bad creds")).when(authenticationManager).authenticate(any());

        assertThrows(BadCredentialsException.class,
                () -> authService.login(new LoginRequest("nobody@example.com", "whatever")));

        verify(userRepository, never()).save(any());
    }

    @Test
    void signup_createsAnActiveUserOnTheFreePlan_andIssuesATemporaryPassword() {
        SignupRequest request = new SignupRequest("Arvind", "Kumar", "arvind@example.com", "9999999999");
        when(userRepository.existsByEmail("arvind@example.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("placeholder-hash");
        when(userRepository.save(any())).thenAnswer(inv -> {
            User saved = inv.getArgument(0);
            saved.setId(9L);
            return saved;
        });

        SignupResponse response = authService.signup(request);

        assertEquals("Arvind Kumar", response.user().name());
        assertEquals("arvind@example.com", response.user().email());
        verify(subscriptionService).assignFreePlan(any(User.class));
        verify(passwordResetService).issueSignupTemporaryPassword(any(User.class));
    }

    @Test
    void signup_rejectsADuplicateEmail() {
        SignupRequest request = new SignupRequest("Arvind", "Kumar", "arvind@example.com", "9999999999");
        when(userRepository.existsByEmail("arvind@example.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.signup(request));

        verify(userRepository, never()).save(any());
        verify(passwordResetService, never()).issueSignupTemporaryPassword(any());
    }
}
