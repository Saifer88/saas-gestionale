package com.pirris.dto.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/** Input for creating or updating a service rate. Price in cents. */
public record ServiceRateDraftDto(
        @NotBlank String name,
        @PositiveOrZero long priceCents,
        @Positive Integer durationMinutes) {
}
