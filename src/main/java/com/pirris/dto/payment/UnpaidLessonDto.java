package com.pirris.dto.payment;

import java.time.LocalDateTime;
import java.util.UUID;

/** A lesson participation not yet paid and not covered by a package. Price in cents. */
public record UnpaidLessonDto(
        UUID participantId,
        UUID sessionId,
        LocalDateTime startDate,
        UUID serviceId,
        long priceCents) {
}
