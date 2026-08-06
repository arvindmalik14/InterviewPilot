package com.malik.InterviewPilot.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class PasswordPolicyValidatorTest {

    private final PasswordPolicyValidator validator = new PasswordPolicyValidator();

    @Test
    void accepts_aPasswordMeetingEveryRule() {
        assertTrue(validator.isValid("Password@123", mock()));
    }

    @Test
    void rejects_null() {
        assertFalse(validator.isValid(null, mock()));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Pass@1",              // too short (6 chars)
            "Aa1@aaaaaaaaaaaaaaaaa", // too long (21 chars)
            "password@123",        // no uppercase
            "PASSWORD@123",        // no lowercase
            "Password@@@@",        // no digit
            "Password123"          // no special character
    })
    void rejects_passwordsMissingAnyRule(String password) {
        assertFalse(validator.isValid(password, mock()));
    }
}
