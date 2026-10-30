package com.pirris.dto.course;

import java.time.LocalDateTime;
import java.util.UUID;

/** One enrollment of a client in a course. */
public record CourseParticipantDto(
        UUID id,
        UUID courseId,
        UUID clientId,
        LocalDateTime joinedOn,
        UUID packageId,
        boolean active,
        String notes) {
}
