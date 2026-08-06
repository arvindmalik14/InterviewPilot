package com.malik.InterviewPilot.dto.auth;

import com.malik.InterviewPilot.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
        @NotBlank(message = "Email is required") @Email(message = "Email must be valid") String email,
        @NotBlank(message = "New password is required") @ValidPassword String newPassword,
        @NotBlank(message = "Confirm password is required") String confirmPassword
) {
}
