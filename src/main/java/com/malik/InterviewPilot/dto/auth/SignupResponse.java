package com.malik.InterviewPilot.dto.auth;

import com.malik.InterviewPilot.dto.user.UserResponse;

/** No token here on purpose — the account has only a temporary password, mailed separately. */
public record SignupResponse(
        UserResponse user,
        String message
) {
}
