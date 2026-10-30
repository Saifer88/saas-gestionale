package com.pirris.dto.agenda;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.pirris.model.enums.SessionStatus;

/** Full session (individual or group occurrence) with its participants. */
public record SessionDetailDto(
        UUID id,
        LocalDateTime startDate,
        int durationMinutes,
        UUID serviceId,
        UUID courseId,
        String notes,
        SessionStatus status,
        LocalDateTime invoiceDate,
        List<SessionParticipantDto> participants) {
}
