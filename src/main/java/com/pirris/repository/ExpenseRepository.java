package com.pirris.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.pirris.model.ExpenseModel;

import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Tenant-scoped data access for {@link ExpenseModel}. All reads are confined to
 * the current studio and all writes are stamped with it (see
 * {@link TenantRepository}).
 */
@ApplicationScoped
public class ExpenseRepository extends TenantRepository<ExpenseModel> {

    @Override
    protected void setStudioId(ExpenseModel entity, UUID studioId) {
        entity.studioId = studioId;
    }

    /**
     * Expenses of the current studio whose date falls in
     * {@code [fromInclusive, toExclusive)}, newest first. Either bound may be
     * null, meaning unbounded on that side.
     */
    public List<ExpenseModel> listInRange(LocalDateTime fromInclusive, LocalDateTime toExclusive) {
        List<String> clauses = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        if (fromInclusive != null) {
            clauses.add("date >= :from");
            params.put("from", fromInclusive);
        }
        if (toExclusive != null) {
            clauses.add("date < :to");
            params.put("to", toExclusive);
        }
        String hql = clauses.isEmpty() ? null : String.join(" and ", clauses);
        return queryForTenant(hql, Sort.descending("date"), params).list();
    }
}
