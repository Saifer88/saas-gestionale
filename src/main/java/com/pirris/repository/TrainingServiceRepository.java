package com.pirris.repository;

import java.util.List;
import java.util.UUID;

import com.pirris.model.TrainingServiceModel;

import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Tenant-scoped data access for {@link TrainingServiceModel}. All reads are
 * confined to the current studio and all writes are stamped with it (see
 * {@link TenantRepository}).
 */
@ApplicationScoped
public class TrainingServiceRepository extends TenantRepository<TrainingServiceModel> {

    @Override
    protected void setStudioId(TrainingServiceModel entity, UUID studioId) {
        entity.studioId = studioId;
    }

    /** Services of the current studio ordered by name; archived ones only on request. */
    public List<TrainingServiceModel> list(boolean includeArchived) {
        return queryForTenant(includeArchived ? null : "archived = false",
                Sort.by("name"), null).list();
    }
}
