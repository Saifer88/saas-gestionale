package com.pirris.dto.lessonpackage;

import java.time.LocalDateTime;
import java.util.UUID;

import com.pirris.model.enums.PackageKind;
import com.pirris.model.enums.PaymentMethod;

/**
 * A prepaid package as shown in the Pacchetti screen. Price in cents.
 * {@code usedCredits} / {@code remainingCredits} are derived (capacity minus
 * participants with this package whose session is completed), never stored.
 */
public record LessonPackageDto(
        UUID id,
        UUID clientId,
        LocalDateTime purchasedOn,
        long priceCents,
        int capacity,
        LocalDateTime expiresOn,
        String notes,
        PaymentMethod paymentMethod,
        LocalDateTime invoiceDate,
        boolean black,
        PackageKind kind,
        int usedCredits,
        int remainingCredits) {
}
