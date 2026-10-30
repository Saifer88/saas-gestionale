package com.pirris.dto.payment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Derived account statement of one client over a period. All amounts in cents;
 * positive balances are owed by the client.
 */
public record StatementDto(
        UUID clientId,
        LocalDate from,
        LocalDate to,
        long openingBalanceCents,
        long closingBalanceCents,
        List<Entry> entries) {

    /** One derived movement (lesson charge, package purchase, ...). */
    public record Entry(
            LocalDateTime date,
            String type,
            String description,
            long amountCents) {
    }
}
