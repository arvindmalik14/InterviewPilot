package com.malik.InterviewPilot.razorpay.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/** isActive is optional: null means "leave/default active" on create, "leave unchanged" on update. */
public record PlanRequest(
        @NotBlank(message = "Plan name is required") String planName,
        @NotNull(message = "Price is required") @DecimalMin(value = "0.0", message = "Price cannot be negative") BigDecimal price,
        @NotNull(message = "Duration in months is required") @Positive Integer durationInMonths,
        @NotNull(message = "Question limit is required") @Positive Integer questionLimit,
        Boolean isActive
) {
}
