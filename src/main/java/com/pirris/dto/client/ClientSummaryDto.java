package com.pirris.dto.client;

import java.util.UUID;

/**
 * One row of the Clienti list. A lightweight projection of a client used by the
 * list/search views; the full profile lives in {@link ClientDetailDto}.
 *
 * @param id           client identifier
 * @param firstName    given name
 * @param lastName     family name
 * @param phone        primary phone (may be empty)
 * @param email        primary email (may be empty)
 * @param archived     whether the client is archived (soft-deleted)
 * @param balanceCents outstanding balance in cents (positive = owed by the
 *                     client); derived, see {@code ClientService}
 */
public record ClientSummaryDto(
        UUID id,
        String firstName,
        String lastName,
        String phone,
        String email,
        boolean archived,
        long balanceCents) {
}
