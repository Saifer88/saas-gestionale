package com.pirris.dto.service;

import java.util.List;
import java.util.UUID;

/** A training service of the catalogue with its rates (Servizi screen). */
public record TrainingServiceDto(
        UUID id,
        String name,
        String description,
        int durationMinutes,
        String colorHex,
        boolean archived,
        List<ServiceRateDto> rates) {
}
