package com.malik.InterviewPilot.dto.auth;

import com.malik.InterviewPilot.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(
        @NotBlank(message = "Name is required") String name,
        @NotBlank(message = "Email is required") @Email(message = "Email must be valid") String email,
        @NotBlank(message = "Mobile number is required")
        @Pattern(regexp = "^[0-9]{10,15}$", message = "Mobile number must be 10-15 digits") String mobileNumber,
        @NotBlank(message = "Password is required") @ValidPassword String password
) {
}
