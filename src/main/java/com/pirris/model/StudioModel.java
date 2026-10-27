package com.pirris.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

/**
 * A tenant: an independent studio whose data is fully isolated from other
 * studios (table {@code studio}). The studio id is the discriminator applied to
 * every tenant-scoped entity via Hibernate {@code @TenantId}.
 *
 * <p>Designed to host more than one operator account in the future; for now a
 * studio typically has a single account.</p>
 */
@Entity
@Table(name = "studio")
public class StudioModel extends AuditedModel {

    @NotBlank
    @Column(name = "name", nullable = false)
    public String name;

    @Column(name = "is_active", nullable = false)
    public boolean active = true;
}
