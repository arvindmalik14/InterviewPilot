package com.malik.InterviewPilot.security;

import com.malik.InterviewPilot.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Getter
public class UserPrincipal implements UserDetails {

    private final User user;

    public UserPrincipal(User user) {
        this.user = user;
    }

    public Long getId() {
        return user.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * Backs account-lockout: DaoAuthenticationProvider checks this before comparing credentials,
     * so a locked account is rejected (via LockedException) without ever touching the password.
     * The lock is self-expiring — once accountLockedUntil is in the past, this returns true again.
     */
    @Override
    public boolean isAccountNonLocked() {
        Instant lockedUntil = user.getAccountLockedUntil();
        return lockedUntil == null || lockedUntil.isBefore(Instant.now());
    }

    /** Backs the "active" flag: DaoAuthenticationProvider rejects a disabled account via DisabledException. */
    @Override
    public boolean isEnabled() {
        return user.isActive();
    }
}
