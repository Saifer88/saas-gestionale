package com.pirris.model;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

/**
 * A training discipline/service offered by the business
 * (table {@code training_service}).
 */
@Entity
@Table(name = "training_service")
public class TrainingServiceModel extends TenantModel {

    @NotBlank
    @Column(name = "name", nullable = false)
    public String name;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "description")
    public String description = "";

    @Column(name = "duration_minutes", nullable = false)
    public int durationMinutes = 60;

    @Column(name = "color_hex")
    public String colorHex = "";

    @Column(name = "is_archived", nullable = false)
    public boolean archived = false;
}
