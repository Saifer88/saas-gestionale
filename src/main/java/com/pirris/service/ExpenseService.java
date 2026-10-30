package com.pirris.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.pirris.dto.expense.ExpenseDraftDto;
import com.pirris.dto.expense.ExpenseDto;
import com.pirris.mapper.ExpenseMapper;
import com.pirris.model.ExpenseModel;
import com.pirris.repository.ExpenseRepository;
import com.pirris.resource.error.NotFoundException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Business logic for the Spese view. All data access goes through
 * {@link ExpenseRepository}, so every operation is confined to the current
 * studio. Entity/DTO conversion is delegated to {@link ExpenseMapper}.
 *
 * <p>Reads are implemented; writes are stubs until the write flow is built.</p>
 */
@ApplicationScoped
public class ExpenseService {

    @Inject
    ExpenseRepository expenses;

    @Inject
    ExpenseMapper mapper;

    /**
     * Expenses of the studio, newest first, optionally restricted to a date
     * range. Both bounds are inclusive calendar days; either may be null.
     */
    public List<ExpenseDto> list(LocalDate from, LocalDate to) {
        return mapper.toDtos(expenses.listInRange(
                from != null ? from.atStartOfDay() : null,
                to != null ? to.plusDays(1).atStartOfDay() : null));
    }

    /** One expense; 404 when it does not belong to the studio. */
    public ExpenseDto detail(UUID id) {
        return mapper.toDto(requireExpense(id));
    }

    /** Creates an expense from the draft. */
    public ExpenseDto create(ExpenseDraftDto draft) {
        // TODO: map the draft onto a new ExpenseModel and persist it for the tenant.
        throw new UnsupportedOperationException("ExpenseService.create not yet implemented");
    }

    /** Updates an existing expense from the draft. */
    public ExpenseDto update(UUID id, ExpenseDraftDto draft) {
        // TODO: require the expense in the studio, apply the draft.
        throw new UnsupportedOperationException("ExpenseService.update not yet implemented");
    }

    /** Deletes an expense (physical: the table has no archived flag). */
    public void delete(UUID id) {
        // TODO: deleteByIdForTenant, 404 when nothing was removed.
        throw new UnsupportedOperationException("ExpenseService.delete not yet implemented");
    }

    private ExpenseModel requireExpense(UUID id) {
        return expenses.findByIdForTenant(id)
                .orElseThrow(() -> NotFoundException.of("Expense", id));
    }
}
