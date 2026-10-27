package com.pirris.dto.client;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * Full client profile for the Clienti detail view: personal data, medical
 * texts, scheduling preferences, derived balance and activity history.
 *
 * <p>All monetary amounts are in cents. The {@code balanceCents} and
 * {@code history} fields are <b>derived</b> from sessions, packages and
 * expenses; their population is owned by the accounting logic (Phase 3), not by
 * the client context itself.</p>
 */
public record ClientDetailDto(
        UUID id,
        String firstName,
        String lastName,
        String phone,
        String email,
        String taxCode,
        String billingAddress,
        LocalDate birthDate,
        String notes,
        String anamnesis,
        String physicalAnalysis,
        UUID preferredServiceId,
        UUID preferredRateId,
        UUID preferredPackageId,
        LocalTime preferredTime,
        Integer preferredDurationMinutes,
        Long preferredPriceCents,
        LocalDateTime joinedOn,
        boolean archived,
        long balanceCents,
        List<HistoryEntry> history) {

    /**
     * One derived event on the client's account timeline (a completed lesson, a
     * package purchase, ...), expressed as a signed amount in cents.
     *
     * @param date        when the event occurred
     * @param type        event kind token (e.g. {@code "lesson"}, {@code "package"})
     * @param description human-readable label
     * @param amountCents signed amount in cents (positive = charge to the client,
     *                    negative = payment received)
     */
    public record HistoryEntry(
            LocalDateTime date,
            String type,
            String description,
            long amountCents) {
    }
}
