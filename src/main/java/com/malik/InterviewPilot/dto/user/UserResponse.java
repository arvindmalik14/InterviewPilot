package com.malik.InterviewPilot.dto.user;

import com.malik.InterviewPilot.entity.Role;
import com.malik.InterviewPilot.entity.User;

public record UserResponse(
        Long id,
        String name,
        String email,
        Role role
) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}
