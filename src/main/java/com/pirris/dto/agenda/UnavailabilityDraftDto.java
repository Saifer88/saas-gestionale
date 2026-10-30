package com.pirris.dto.agenda;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

/** Input for creating or updating an unavailability block. */
public record UnavailabilityDraftDto(
        @NotNull LocalDateTime startDate,
        @NotNull LocalDateTime endDate,
        String reason,
        boolean allDay,
        String notes) {
}
