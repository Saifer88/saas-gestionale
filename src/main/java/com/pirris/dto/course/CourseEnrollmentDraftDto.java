package com.pirris.dto.course;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

/** Input for enrolling a client in a course. {@code joinedOn} defaults to now when absent. */
public record CourseEnrollmentDraftDto(
        @NotNull UUID clientId,
        UUID packageId,
        LocalDateTime joinedOn,
        String notes) {
}
