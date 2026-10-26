package com.pirris.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;

/**
 * Input payload to create or update a client. Mirrors the frontend's
 * {@code ClientDraft}: organisational notes stay separate from the reserved
 * medical texts ({@code anamnesis}, {@code physicalAnalysis}).
 */
public record ClientDraftDto(
        @NotBlank String firstName,
        @NotBlank String lastName,
        String phone,
        String email,
        String taxCode,
        String billingAddress,
        LocalDate birthDate,
        String notes,
        String anamnesis,
        String physicalAnalysis,
        UUID preferredServiceId,
        UUID preferredRateId,
        LocalDateTime joinedOn) {
}
