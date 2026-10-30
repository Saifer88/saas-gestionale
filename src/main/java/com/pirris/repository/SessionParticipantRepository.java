package com.pirris.repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.pirris.model.SessionParticipantModel;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Tenant-scoped data access for {@link SessionParticipantModel}. All reads are
 * confined to the current studio and all writes are stamped with it (see
 * {@link TenantRepository}).
 */
@ApplicationScoped
public class SessionParticipantRepository extends TenantRepository<SessionParticipantModel> {

    @Override
    protected void setStudioId(SessionParticipantModel entity, UUID studioId) {
        entity.studioId = studioId;
    }

    /** Participants of the given sessions (one query). Empty when no session id is given. */
    public List<SessionParticipantModel> listBySessions(Collection<UUID> sessionIds) {
        if (sessionIds == null || sessionIds.isEmpty()) {
            return List.of();
        }
        return queryForTenant("sessionId in :ids", null, Map.of("ids", sessionIds)).list();
    }

    /** Participants of one session. */
    public List<SessionParticipantModel> listBySession(UUID sessionId) {
        // ":sid" is reserved for the tenant parameter added by queryForTenant.
        return queryForTenant("sessionId = :sessionId", null, Map.of("sessionId", sessionId)).list();
    }
}
