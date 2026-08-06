package com.malik.InterviewPilot.service;

import com.malik.InterviewPilot.dto.auth.AuthResponse;
import com.malik.InterviewPilot.dto.auth.LoginRequest;
import com.malik.InterviewPilot.dto.auth.RegisterRequest;
import com.malik.InterviewPilot.dto.auth.SignupRequest;
import com.malik.InterviewPilot.dto.auth.SignupResponse;
import com.malik.InterviewPilot.dto.user.UserResponse;
import com.malik.InterviewPilot.entity.Role;
import com.malik.InterviewPilot.entity.User;
import com.malik.InterviewPilot.exception.DuplicateResourceException;
import com.malik.InterviewPilot.razorpay.service.SubscriptionService;
import com.malik.InterviewPilot.repository.UserRepository;
import com.malik.InterviewPilot.security.JwtService;
import com.malik.InterviewPilot.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final SubscriptionService subscriptionService;
    private final PasswordResetService passwordResetService;

    @Value("${app.security.max-failed-login-attempts:5}")
    private int maxFailedLoginAttempts;

    @Value("${app.security.account-lockout-minutes:15}")
    private long accountLockoutMinutes;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("An account with this email already exists");
        }

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .mobileNumber(request.mobileNumber())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build();

        user = userRepository.save(user);
        subscriptionService.assignFreePlan(user);

        String token = jwtService.generateToken(new UserPrincipal(user));
        return new AuthResponse(token, UserResponse.from(user), false);
    }

    /**
     * Admin-style onboarding: no password is collected from the user — a temporary password is
     * generated, hashed, and emailed (see PasswordResetService), and the account starts with
     * passwordResetRequired=true so the first login forces a real password to be set.
     * The primary `password` column is filled with a random, never-communicated placeholder hash
     * (never null) so it can never itself be used to log in.
     */
    @Transactional
    public SignupResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("An account with this email already exists");
        }

        User user = User.builder()
                .name(request.firstName() + " " + request.lastName())
                .email(request.email())
                .mobileNumber(request.mobileNumber())
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .role(Role.USER)
                .build();

        user = userRepository.save(user);
        subscriptionService.assignFreePlan(user);
        passwordResetService.issueSignupTemporaryPassword(user);
        log.info("New account signed up, id={}", user.getId());

        return new SignupResponse(
                UserResponse.from(user), "Account created. Check your email for your temporary password.");
    }

    /**
     * Tries the primary password first; on a bad-credentials result (never on a lockout — that
     * rejects outright before any password is checked, see UserPrincipal.isAccountNonLocked),
     * falls back to a still-valid temporary password from the forgot-password flow. Any failure
     * of both counts as one failed attempt towards account lockout.
     */
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email()).orElse(null);

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password()));
            clearFailedAttempts(user);
            log.info("Login succeeded for user id={}", user.getId());
            return new AuthResponse(jwtService.generateToken(new UserPrincipal(user)), UserResponse.from(user), false);
        } catch (LockedException ex) {
            log.warn("Login rejected for user id={} — account is locked", user != null ? user.getId() : null);
            throw ex;
        } catch (BadCredentialsException ex) {
            if (user != null && isValidTemporaryPassword(user, request.password())) {
                clearFailedAttempts(user);
                log.info("Login succeeded via temporary password for user id={}", user.getId());
                return new AuthResponse(jwtService.generateToken(new UserPrincipal(user)), UserResponse.from(user), true);
            }
            recordFailedAttempt(user);
            throw ex;
        }
    }

    private boolean isValidTemporaryPassword(User user, String rawPassword) {
        return user.getTemporaryPasswordHash() != null
                && user.getTemporaryPasswordExpiry() != null
                && user.getTemporaryPasswordExpiry().isAfter(Instant.now())
                && passwordEncoder.matches(rawPassword, user.getTemporaryPasswordHash());
    }

    private void clearFailedAttempts(User user) {
        if (user == null) {
            return;
        }
        if (user.getFailedLoginAttempts() != 0 || user.getAccountLockedUntil() != null) {
            user.setFailedLoginAttempts(0);
            user.setAccountLockedUntil(null);
            userRepository.save(user);
        }
    }

    private void recordFailedAttempt(User user) {
        if (user == null) {
            return;
        }
        // A lock that has already expired shouldn't count against the next failure.
        if (user.getAccountLockedUntil() != null && user.getAccountLockedUntil().isBefore(Instant.now())) {
            user.setAccountLockedUntil(null);
            user.setFailedLoginAttempts(0);
        }

        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);
        if (attempts >= maxFailedLoginAttempts) {
            user.setAccountLockedUntil(Instant.now().plus(Duration.ofMinutes(accountLockoutMinutes)));
            log.warn("Account locked for user id={} after {} failed login attempts", user.getId(), attempts);
        }
        userRepository.save(user);
    }
}
