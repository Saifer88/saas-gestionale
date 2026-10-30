package com.pirris.dto.course;

import java.time.LocalTime;
import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** Input for creating or updating a course template. {@code weekday}: 1 = Monday .. 7 = Sunday. */
public record CourseDraftDto(
        @NotBlank String name,
        @NotNull @Min(1) @Max(7) Integer weekday,
        @NotNull LocalTime startTime,
        @Positive Integer durationMinutes,
        @Positive Integer maxCapacity,
        String location,
        UUID serviceId,
        String notes) {
}
