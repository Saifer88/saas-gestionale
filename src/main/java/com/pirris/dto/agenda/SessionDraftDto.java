package com.pirris.dto.agenda;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.pirris.model.enums.SessionStatus;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** Input for creating or updating a session together with its participants. */
public record SessionDraftDto(
        @NotNull LocalDateTime startDate,
        @Positive Integer durationMinutes,
        UUID serviceId,
        UUID courseId,
        String notes,
        SessionStatus status,
        LocalDateTime invoiceDate,
        List<@Valid SessionParticipantDraftDto> participants) {
}
