package com.pirris.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.pirris.model.BaseModel;
import com.pirris.security.TenantContext;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;

/**
 * Base repository that enforces tenant isolation for a {@link BaseModel} entity
 * whose table has a {@code studio_id} column.
 *
 * <p>Every read is constrained to the current studio and every write is stamped
 * with it, from {@link TenantContext}. Concrete repositories (and the services
 * that use them) must go through these methods and must not call
 * {@code findAll()} / {@code listAll()} on the entity directly, which would
 * ignore the tenant. Deny by default: with no authenticated tenant the context
 * throws before any query runs.</p>
 *
 * @param <T> the tenant-scoped entity type
 */
public abstract class TenantRepository<T extends BaseModel> implements PanacheRepositoryBase<T, UUID> {

    @Inject
    TenantContext tenant;

    /** Reflectively reads/writes the {@code studioId} field shared by tenant entities. */
    protected abstract void setStudioId(T entity, UUID studioId);

    // ---- Reads (always tenant-filtered) ----

    public List<T> listAllForTenant(Sort sort) {
        return find("studioId = :sid", sort, Parameters.with("sid", tenant.currentStudioId())).list();
    }

    public Optional<T> findByIdForTenant(UUID id) {
        return find("studioId = :sid and id = :id",
                Parameters.with("sid", tenant.currentStudioId()).and("id", id))
                .firstResultOptional();
    }

    /**
     * Builds a query scoped to the current tenant. {@code extraQuery} is an
     * additional HQL fragment (without the tenant clause); use {@code :name}
     * parameters and pass them in {@code params}. The tenant parameter {@code sid}
     * is added automatically.
     */
    public PanacheQuery<T> queryForTenant(String extraQuery, Sort sort, Parameters params) {
        Parameters merged = params == null ? Parameters.with("sid", tenant.currentStudioId())
                : params.and("sid", tenant.currentStudioId());
        String hql = (extraQuery == null || extraQuery.isBlank())
                ? "studioId = :sid"
                : "studioId = :sid and (" + extraQuery + ")";
        return sort == null ? find(hql, merged) : find(hql, sort, merged);
    }

    public long countForTenant() {
        return count("studioId = :sid", Parameters.with("sid", tenant.currentStudioId()));
    }

    // ---- Writes (always tenant-stamped) ----

    /** Stamps the current tenant and persists. Use instead of {@code entity.persist()}. */
    public void persistForTenant(T entity) {
        setStudioId(entity, tenant.currentStudioId());
        persist(entity);
    }

    /** Deletes an entity by id only if it belongs to the current tenant; returns true if removed. */
    public boolean deleteByIdForTenant(UUID id) {
        return delete("studioId = :sid and id = :id",
                Parameters.with("sid", tenant.currentStudioId()).and("id", id)) > 0;
    }
}
