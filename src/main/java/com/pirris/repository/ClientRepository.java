package com.pirris.repository;

import java.util.List;
import java.util.UUID;

import com.pirris.model.ClientModel;

import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Tenant-scoped data access for {@link ClientModel}. All reads are confined to
 * the current studio and all writes are stamped with it (see
 * {@link TenantRepository}).
 */
@ApplicationScoped
public class ClientRepository extends TenantRepository<ClientModel> {

    @Override
    protected void setStudioId(ClientModel entity, UUID studioId) {
        entity.studioId = studioId;
    }

    /** Active (non-archived) clients of the current studio, ordered by name. */
    public List<ClientModel> listActive() {
        return queryForTenant("archived = false",
                Sort.by("lastName").and("firstName"), null).list();
    }

    /** Case-insensitive search within the current studio over name, phone, email. */
    public List<ClientModel> search(String term) {
        String like = "%" + term.trim().toLowerCase() + "%";
        return queryForTenant(
                "archived = false and ("
                        + "lower(firstName) like :like or lower(lastName) like :like "
                        + "or lower(phone) like :like or lower(email) like :like)",
                Sort.by("lastName").and("firstName"),
                Parameters.with("like", like)).list();
    }
}
