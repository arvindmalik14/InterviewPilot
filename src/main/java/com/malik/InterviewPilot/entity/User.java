package com.malik.InterviewPilot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "mobile_number", length = 20)
    private String mobileNumber;

    /** Admin-controlled enable/disable switch — checked via UserPrincipal.isEnabled(). */
    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private Role role = Role.USER;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    /** BCrypt hash of a forgot-password temporary password — never the plaintext value. */
    @Column(name = "temporary_password", length = 255)
    private String temporaryPasswordHash;

    @Column(name = "temporary_password_expiry")
    private Instant temporaryPasswordExpiry;

    @Column(name = "password_reset_required", nullable = false)
    @Builder.Default
    private boolean passwordResetRequired = false;

    /** Failed-login counter for account lockout; reset on any successful login. */
    @Column(name = "failed_login_attempts", nullable = false)
    @Builder.Default
    private int failedLoginAttempts = 0;

    @Column(name = "account_locked_until")
    private Instant accountLockedUntil;

    /** JWTs issued before this instant are rejected — the invalidation mechanism for a stateless token. */
    @Column(name = "password_changed_at", nullable = false)
    @Builder.Default
    private Instant passwordChangedAt = Instant.now();
}
