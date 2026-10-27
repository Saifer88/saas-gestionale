package com.pirris.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * Enrollment of a client in a {@link CourseModel}, optionally backed by a time
 * package (table {@code course_participant}).
 */
@Entity
@Table(name = "course_participant")
public class CourseParticipantModel extends TenantModel {

    @NotNull
    @Column(name = "course_id", nullable = false)
    public UUID courseId;

    @NotNull
    @Column(name = "client_id", nullable = false)
    public UUID clientId;

    @NotNull
    @Column(name = "joined_on", nullable = false)
    public LocalDateTime joinedOn;

    @Column(name = "package_id")
    public UUID packageId;

    @Column(name = "is_active", nullable = false)
    public boolean active = true;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "notes")
    public String notes = "";
}
