package com.pirris.dto.lessonpackage;

import java.time.LocalDateTime;
import java.util.UUID;

import com.pirris.model.enums.PackageKind;
import com.pirris.model.enums.PaymentMethod;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Input for purchasing a package for a client. Price in cents;
 * {@code purchasedOn} defaults to now when absent.
 */
public record PackagePurchaseDraftDto(
        @NotNull UUID clientId,
        LocalDateTime purchasedOn,
        @PositiveOrZero long priceCents,
        @Positive Integer capacity,
        LocalDateTime expiresOn,
        String notes,
        @NotNull PaymentMethod paymentMethod,
        LocalDateTime invoiceDate,
        boolean black,
        @NotNull PackageKind kind) {
}
