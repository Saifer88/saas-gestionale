package com.pirris.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * A calendar block during which the trainer is unavailable
 * (table {@code unavailability}).
 */
@Entity
@Table(name = "unavailability")
public class UnavailabilityModel extends AuditedModel {

    @NotNull
    @Column(name = "start_date", nullable = false)
    public LocalDateTime startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    public LocalDateTime endDate;

    @Column(name = "reason")
    public String reason = "";

    @Column(name = "all_day", nullable = false)
    public boolean allDay = false;

    @Column(name = "notes")
    public String notes = "";
}
