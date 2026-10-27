package com.pirris.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

/**
 * Base class for all tenant-scoped domain entities: an {@link AuditedModel}
 * (id + {@code created_at}/{@code updated_at}) plus the owning studio.
 *
 * <p>{@code studioId} is a plain column. Isolation is enforced in the
 * application layer: all data access goes through a {@code TenantRepository},
 * which always filters by the current studio and stamps it on writes. Never
 * query these entities directly with {@code findAll()} and friends — that would
 * bypass tenant isolation.</p>
 */
@MappedSuperclass
public abstract class TenantModel extends AuditedModel {

    @Column(name = "studio_id", nullable = false, updatable = false)
    public UUID studioId;
}
