package com.pirris.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.pirris.model.UnavailabilityModel;

import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Tenant-scoped data access for {@link UnavailabilityModel}. All reads are
 * confined to the current studio and all writes are stamped with it (see
 * {@link TenantRepository}).
 */
@ApplicationScoped
public class UnavailabilityRepository extends TenantRepository<UnavailabilityModel> {

    @Override
    protected void setStudioId(UnavailabilityModel entity, UUID studioId) {
        entity.studioId = studioId;
    }

    /** Unavailability blocks overlapping {@code [start, end)}, by start time. */
    public List<UnavailabilityModel> listOverlapping(LocalDateTime start, LocalDateTime end) {
        return queryForTenant("startDate < :end and endDate > :start", Sort.by("startDate"),
                Map.of("start", start, "end", end)).list();
    }
}
