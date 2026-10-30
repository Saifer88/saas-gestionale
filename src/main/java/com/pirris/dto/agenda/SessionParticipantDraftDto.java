package com.pirris.dto.agenda;

import java.util.UUID;

import com.pirris.model.enums.PaymentMethod;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/** Input for adding or updating a session participant. Price in cents. */
public record SessionParticipantDraftDto(
        @NotNull UUID clientId,
        @PositiveOrZero long priceCents,
        UUID packageId,
        PaymentMethod paymentMethod,
        boolean paid,
        boolean black) {
}
