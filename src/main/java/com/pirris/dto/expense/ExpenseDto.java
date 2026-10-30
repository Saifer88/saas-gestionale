package com.pirris.dto.expense;

import java.time.LocalDateTime;
import java.util.UUID;

import com.pirris.model.enums.ExpenseKind;

/** One expense as shown in the Spese screen. Amount in cents. */
public record ExpenseDto(
        UUID id,
        String name,
        LocalDateTime date,
        long amountCents,
        ExpenseKind kind,
        String sourceKey,
        boolean personal) {
}
