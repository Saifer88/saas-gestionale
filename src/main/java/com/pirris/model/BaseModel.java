package com.pirris.model;

import java.util.UUID;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;

/**
 * Base class for every domain model entity.
 *
 * <p>Uses a client-assignable {@link UUID} primary key (generated on persist if
 * not already set) to match the {@code UUID} primary keys defined by the
 * Liquibase schema. Schema generation is owned by Liquibase, so entities never
 * drive DDL.</p>
 */
@MappedSuperclass
public abstract class BaseModel extends PanacheEntityBase {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    public UUID id;

    @PrePersist
    void assignId() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }
}
