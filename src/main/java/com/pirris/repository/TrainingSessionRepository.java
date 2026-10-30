package com.pirris.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.pirris.model.TrainingSessionModel;
import com.pirris.model.enums.SessionStatus;

import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Tenant-scoped data access for {@link TrainingSessionModel}. All reads are
 * confined to the current studio and all writes are stamped with it (see
 * {@link TenantRepository}).
 */
@ApplicationScoped
public class TrainingSessionRepository extends TenantRepository<TrainingSessionModel> {

    @Override
    protected void setStudioId(TrainingSessionModel entity, UUID studioId) {
        entity.studioId = studioId;
    }

    /** Non-cancelled sessions starting in {@code [start, end)}, by start time. */
    public List<TrainingSessionModel> listNotCancelledBetween(LocalDateTime start, LocalDateTime end) {
        return queryForTenant("status <> :cancelled and startDate >= :start and startDate < :end",
                Sort.by("startDate"),
                Map.of("cancelled", SessionStatus.CANCELLED, "start", start, "end", end)).list();
    }
}
