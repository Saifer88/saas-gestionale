package com.pirris.dto.payment;

import java.util.UUID;

/**
 * Derived balance of one client, in cents:
 * {@code balanceCents = chargesCents − paymentsCents} (positive = owed by the client).
 */
public record ClientBalanceDto(
        UUID clientId,
        long chargesCents,
        long paymentsCents,
        long balanceCents) {
}
