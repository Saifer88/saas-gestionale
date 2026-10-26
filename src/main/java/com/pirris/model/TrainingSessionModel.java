package com.pirris.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.pirris.model.enums.SessionStatus;
import com.pirris.model.enums.SessionStatusConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * A booked individual or group training appointment
 * (table {@code training_session}).
 */
@Entity
@Table(name = "training_session")
public class TrainingSessionModel extends AuditedModel {

    @NotNull
    @Column(name = "start_date", nullable = false)
    public LocalDateTime startDate;

    @Column(name = "duration_minutes", nullable = false)
    public int durationMinutes = 60;

    @Column(name = "service_id")
    public UUID serviceId;

    /**
     * When set, this session is a concrete occurrence of a recurring
     * {@link CourseModel}; when {@code null} it is a standalone appointment.
     * Lets the whole session engine (attendance, completion, calendar) serve
     * courses too, without a parallel model.
     */
    @Column(name = "course_id")
    public UUID courseId;

    @Column(name = "notes", nullable = false)
    public String notes = "";

    /**
     * Lifecycle status, stored in {@code status_raw} via
     * {@link SessionStatusConverter} as its raw token.
     */
    @NotNull
    @Convert(converter = SessionStatusConverter.class)
    @Column(name = "status_raw", nullable = false)
    public SessionStatus status = SessionStatus.PLANNED;

    @Column(name = "invoice_date")
    public LocalDateTime invoiceDate;
}
