package com.malik.InterviewPilot.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService("test-only-secret-key-at-least-32-characters-long", 60_000L);
        userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user@example.com");
    }

    @Test
    void isTokenValid_true_whenNoInvalidationCutoffGiven() {
        String token = jwtService.generateToken(userDetails);
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void isTokenValid_true_whenTokenIssuedAfterCutoff() {
        String token = jwtService.generateToken(userDetails);
        Instant passwordChangedInThePast = Instant.now().minus(1, ChronoUnit.HOURS);
        assertTrue(jwtService.isTokenValid(token, userDetails, passwordChangedInThePast));
    }

    /** This is the mechanism that "invalidates all active sessions" after a password reset. */
    @Test
    void isTokenValid_false_whenTokenWasIssuedBeforeAPasswordChange() {
        String token = jwtService.generateToken(userDetails);
        Instant passwordChangedInTheFuture = Instant.now().plus(1, ChronoUnit.HOURS);
        assertFalse(jwtService.isTokenValid(token, userDetails, passwordChangedInTheFuture));
    }

    @Test
    void isTokenValid_false_whenUsernameDoesNotMatch() {
        String token = jwtService.generateToken(userDetails);
        UserDetails otherUser = mock(UserDetails.class);
        when(otherUser.getUsername()).thenReturn("someone-else@example.com");
        assertFalse(jwtService.isTokenValid(token, otherUser));
    }
}
