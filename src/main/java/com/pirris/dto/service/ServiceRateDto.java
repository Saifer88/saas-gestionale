package com.pirris.dto.service;

import java.util.UUID;

/** One rate (price/duration) of a training service. Price in cents. */
public record ServiceRateDto(
        UUID id,
        UUID serviceId,
        String name,
        long priceCents,
        int durationMinutes,
        boolean archived) {
}
