package com.pirris.dto.course;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/** Full course template with its active enrollments. */
public record CourseDetailDto(
        UUID id,
        String name,
        int weekday,
        LocalTime startTime,
        int durationMinutes,
        int maxCapacity,
        String location,
        UUID serviceId,
        boolean active,
        String notes,
        List<CourseParticipantDto> participants) {
}
