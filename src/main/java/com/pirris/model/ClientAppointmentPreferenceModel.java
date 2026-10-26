package com.pirris.model;

import java.time.LocalTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * A recurring weekly time-slot a client prefers for appointments
 * (table {@code client_appointment_preference}).
 */
@Entity
@Table(name = "client_appointment_preference")
public class ClientAppointmentPreferenceModel extends AuditedModel {

    @NotNull
    @Column(name = "client_id", nullable = false)
    public UUID clientId;

    /** ISO day of week: 1 = Monday ... 7 = Sunday. */
    @Min(1)
    @Max(7)
    @Column(name = "weekday", nullable = false)
    public int weekday;

    @NotNull
    @Column(name = "start_time", nullable = false)
    public LocalTime startTime;

    @Column(name = "end_time")
    public LocalTime endTime;

    @Column(name = "notes")
    public String notes = "";
}
