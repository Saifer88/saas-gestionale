package com.pirris.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * A pricing tier / duration option belonging to a {@link TrainingServiceModel}
 * (table {@code service_rate}).
 */
@Entity
@Table(name = "service_rate")
public class ServiceRateModel extends TenantModel {

    @NotNull
    @Column(name = "service_id", nullable = false)
    public UUID serviceId;

    @NotBlank
    @Column(name = "name", nullable = false)
    public String name;

    @PositiveOrZero
    @Column(name = "price_cents", nullable = false)
    public long priceCents = 0;

    @Column(name = "duration_minutes", nullable = false)
    public int durationMinutes = 60;

    @Column(name = "is_archived", nullable = false)
    public boolean archived = false;
}
