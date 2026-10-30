package com.pirris.dto.course;

import java.time.LocalTime;
import java.util.UUID;

/** One row of the Corsi list: the weekly recurrence template of a course. */
public record CourseSummaryDto(
        UUID id,
        String name,
        int weekday,
        LocalTime startTime,
        int durationMinutes,
        int maxCapacity,
        String location,
        UUID serviceId,
        boolean active) {
}
