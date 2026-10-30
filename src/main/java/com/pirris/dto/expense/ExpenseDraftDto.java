package com.pirris.dto.expense;

import java.time.LocalDateTime;

import com.pirris.model.enums.ExpenseKind;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/** Input for creating or updating an expense. Amount in cents. */
public record ExpenseDraftDto(
        @NotBlank String name,
        @NotNull LocalDateTime date,
        @PositiveOrZero long amountCents,
        @NotNull ExpenseKind kind,
        String sourceKey,
        boolean personal) {
}
