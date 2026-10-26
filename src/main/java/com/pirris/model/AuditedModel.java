package com.pirris.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

/**
 * Base class for entities that carry {@code created_at} / {@code updated_at}
 * auditing columns. Both timestamps are maintained automatically by JPA
 * lifecycle callbacks.
 *
 * <p>Note: a few tables in the schema ({@code lesson_package},
 * {@code session_participant}) intentionally do not have audit columns, so the
 * corresponding models extend {@link BaseModel} directly instead.</p>
 */
@MappedSuperclass
public abstract class AuditedModel extends BaseModel {

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
