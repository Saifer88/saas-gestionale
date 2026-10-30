package com.pirris.repository;

import java.util.List;
import java.util.UUID;

import com.pirris.model.CourseModel;

import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Tenant-scoped data access for {@link CourseModel}. All reads are confined to
 * the current studio and all writes are stamped with it (see
 * {@link TenantRepository}).
 */
@ApplicationScoped
public class CourseRepository extends TenantRepository<CourseModel> {

    @Override
    protected void setStudioId(CourseModel entity, UUID studioId) {
        entity.studioId = studioId;
    }

    /** Courses of the current studio by weekday and time; inactive ones only on request. */
    public List<CourseModel> list(boolean includeInactive) {
        return queryForTenant(includeInactive ? null : "active = true",
                Sort.by("weekday").and("startTime"), null).list();
    }
}
