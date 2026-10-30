package com.pirris.repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.pirris.model.CourseParticipantModel;

import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Tenant-scoped data access for {@link CourseParticipantModel}. All reads are
 * confined to the current studio and all writes are stamped with it (see
 * {@link TenantRepository}).
 */
@ApplicationScoped
public class CourseParticipantRepository extends TenantRepository<CourseParticipantModel> {

    @Override
    protected void setStudioId(CourseParticipantModel entity, UUID studioId) {
        entity.studioId = studioId;
    }

    /** Active enrollments of one course, oldest first. */
    public List<CourseParticipantModel> listActiveByCourse(UUID courseId) {
        return queryForTenant("courseId = :cid and active = true", Sort.by("joinedOn"),
                Map.of("cid", courseId)).list();
    }
}
