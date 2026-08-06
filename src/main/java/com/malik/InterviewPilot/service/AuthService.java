package com.malik.InterviewPilot.service;

import com.malik.InterviewPilot.dto.auth.AuthResponse;
import com.malik.InterviewPilot.dto.auth.LoginRequest;
import com.malik.InterviewPilot.dto.auth.RegisterRequest;
import com.malik.InterviewPilot.dto.user.UserResponse;
import com.malik.InterviewPilot.entity.Role;
import com.malik.InterviewPilot.entity.User;
import com.malik.InterviewPilot.exception.DuplicateResourceException;
import com.malik.InterviewPilot.razorpay.service.SubscriptionService;
import com.malik.InterviewPilot.repository.UserRepository;
import com.malik.InterviewPilot.security.JwtService;
import com.malik.InterviewPilot.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final SubscriptionService subscriptionService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("An account with this email already exists");
        }

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build();

        user = userRepository.save(user);
        subscriptionService.assignFreePlan(user);

        String token = jwtService.generateToken(new UserPrincipal(user));
        return new AuthResponse(token, UserResponse.from(user));
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));

        String token = jwtService.generateToken(new UserPrincipal(user));
        return new AuthResponse(token, UserResponse.from(user));
    }
}
