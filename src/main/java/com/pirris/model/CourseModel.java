package com.pirris.model;

import java.time.LocalTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * A recurring weekly group course / class (table {@code course}).
 */
@Entity
@Table(name = "course")
public class CourseModel extends AuditedModel {

    @NotBlank
    @Column(name = "name", nullable = false)
    public String name;

    /** ISO day of week: 1 = Monday ... 7 = Sunday. */
    @Min(1)
    @Max(7)
    @Column(name = "weekday", nullable = false)
    public int weekday;

    @NotNull
    @Column(name = "start_time", nullable = false)
    public LocalTime startTime;

    @Column(name = "duration_minutes", nullable = false)
    public int durationMinutes = 60;

    @Column(name = "max_capacity", nullable = false)
    public int maxCapacity = 10;

    @Column(name = "location", nullable = false)
    public String location = "";

    @Column(name = "service_id")
    public UUID serviceId;

    @Column(name = "is_active", nullable = false)
    public boolean active = true;

    @Column(name = "notes")
    public String notes = "";
}
