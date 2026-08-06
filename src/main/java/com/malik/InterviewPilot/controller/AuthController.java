package com.malik.InterviewPilot.controller;

import com.malik.InterviewPilot.dto.auth.AuthResponse;
import com.malik.InterviewPilot.dto.auth.ForgotPasswordRequest;
import com.malik.InterviewPilot.dto.auth.LoginRequest;
import com.malik.InterviewPilot.dto.auth.RegisterRequest;
import com.malik.InterviewPilot.dto.auth.ResetPasswordRequest;
import com.malik.InterviewPilot.dto.auth.SignupRequest;
import com.malik.InterviewPilot.dto.auth.SignupResponse;
import com.malik.InterviewPilot.dto.common.MessageResponse;
import com.malik.InterviewPilot.security.UserPrincipal;
import com.malik.InterviewPilot.service.AuthService;
import com.malik.InterviewPilot.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    /** Admin-style onboarding: no password field — a temporary password is generated and emailed. */
    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signup(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // Stateless JWT: nothing to invalidate server-side, client discards the token.
        return ResponseEntity.ok().build();
    }

    /** Always responds the same way regardless of whether the email is registered (no account enumeration). */
    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.requestPasswordReset(request.email());
        return ResponseEntity.ok(new MessageResponse(
                "If an account exists for that email, a temporary password has been sent to it. "
                        + "Don't have an account yet? Sign up instead."));
    }

    /** Requires the JWT obtained by logging in with the temporary password (see SecurityConfig). */
    @PostMapping("/reset-password")
    public ResponseEntity<AuthResponse> resetPassword(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(passwordResetService.resetPassword(principal.getUser(), request));
    }

    /** Same operation as reset-password (same required auth, same pending-reset gate) under the signup flow's name. */
    @PostMapping("/change-password")
    public ResponseEntity<AuthResponse> changePassword(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(passwordResetService.resetPassword(principal.getUser(), request));
    }

    /** "I never received / lost my temporary password" — always responds the same way (no account enumeration). */
    @PostMapping("/resend-password")
    public ResponseEntity<MessageResponse> resendPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.resendTemporaryPassword(request.email());
        return ResponseEntity.ok(new MessageResponse(
                "If an account exists for that email, a new temporary password has been sent to it."));
    }
}
