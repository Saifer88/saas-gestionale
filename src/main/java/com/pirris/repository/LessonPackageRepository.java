package com.pirris.repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.pirris.model.LessonPackageModel;

import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Tenant-scoped data access for {@link LessonPackageModel}. All reads are
 * confined to the current studio and all writes are stamped with it (see
 * {@link TenantRepository}).
 */
@ApplicationScoped
public class LessonPackageRepository extends TenantRepository<LessonPackageModel> {

    @Override
    protected void setStudioId(LessonPackageModel entity, UUID studioId) {
        entity.studioId = studioId;
    }

    /** Packages of one client, most recent purchase first. */
    public List<LessonPackageModel> listByClient(UUID clientId) {
        return queryForTenant("clientId = :cid", Sort.descending("purchasedOn"),
                Map.of("cid", clientId)).list();
    }

    /** All packages of the studio, most recent purchase first. */
    public List<LessonPackageModel> listAllSorted() {
        return listAllForTenant(Sort.descending("purchasedOn"));
    }
}
