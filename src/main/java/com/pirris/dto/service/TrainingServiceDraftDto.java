package com.pirris.dto.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/** Input for creating or updating a training service. Rates are edited separately. */
public record TrainingServiceDraftDto(
        @NotBlank String name,
        String description,
        @Positive Integer durationMinutes,
        String colorHex) {
}
