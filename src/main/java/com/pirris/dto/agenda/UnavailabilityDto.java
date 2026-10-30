package com.pirris.dto.agenda;

import java.time.LocalDateTime;
import java.util.UUID;

/** One unavailability block of the trainer. */
public record UnavailabilityDto(
        UUID id,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String reason,
        boolean allDay,
        String notes) {
}
