package com.pirris.repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.pirris.model.ServiceRateModel;

import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Tenant-scoped data access for {@link ServiceRateModel}. All reads are
 * confined to the current studio and all writes are stamped with it (see
 * {@link TenantRepository}).
 */
@ApplicationScoped
public class ServiceRateRepository extends TenantRepository<ServiceRateModel> {

    @Override
    protected void setStudioId(ServiceRateModel entity, UUID studioId) {
        entity.studioId = studioId;
    }

    /**
     * Rates of the given services (one query), ordered by price; archived ones
     * only on request. Empty when no service id is given.
     */
    public List<ServiceRateModel> listByServices(Collection<UUID> serviceIds, boolean includeArchived) {
        if (serviceIds == null || serviceIds.isEmpty()) {
            return List.of();
        }
        String hql = includeArchived ? "serviceId in :ids" : "serviceId in :ids and archived = false";
        return queryForTenant(hql, Sort.by("priceCents"), Map.of("ids", serviceIds)).list();
    }
}
