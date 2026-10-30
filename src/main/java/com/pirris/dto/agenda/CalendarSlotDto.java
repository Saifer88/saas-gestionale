package com.pirris.dto.agenda;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.pirris.model.enums.SessionStatus;

/**
 * One block of the Agenda calendar. Sessions and unavailability stay separate
 * entities and are unified only here:
 * <ul>
 *   <li>{@code SESSION}: {@code status}, {@code serviceId}, {@code courseId},
 *       {@code clientIds} are set; {@code reason} is null;</li>
 *   <li>{@code UNAVAILABILITY}: {@code reason} is set; {@code status},
 *       {@code serviceId}, {@code courseId} are null and {@code clientIds} is empty.</li>
 * </ul>
 * Client names are not denormalized: the app resolves them from {@code clientIds}.
 */
public record CalendarSlotDto(
        CalendarSlotType type,
        UUID id,
        LocalDateTime start,
        LocalDateTime end,
        boolean allDay,
        SessionStatus status,
        UUID serviceId,
        UUID courseId,
        List<UUID> clientIds,
        String reason,
        String notes) {
}
