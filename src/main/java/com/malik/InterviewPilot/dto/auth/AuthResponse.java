package com.malik.InterviewPilot.dto.auth;

import com.malik.InterviewPilot.dto.user.UserResponse;

public record AuthResponse(
        String token,
        UserResponse user
) {
}
