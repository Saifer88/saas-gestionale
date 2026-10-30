package com.pirris.dto.agenda;

import java.util.UUID;

import com.pirris.model.enums.PaymentMethod;

/** One participant of a session. Price in cents. */
public record SessionParticipantDto(
        UUID id,
        UUID sessionId,
        UUID clientId,
        long priceCents,
        UUID packageId,
        PaymentMethod paymentMethod,
        boolean paid,
        boolean black) {
}
