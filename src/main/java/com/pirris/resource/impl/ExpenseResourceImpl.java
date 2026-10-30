package com.pirris.resource.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.pirris.dto.expense.ExpenseDraftDto;
import com.pirris.dto.expense.ExpenseDto;
import com.pirris.resource.ExpenseResource;
import com.pirris.service.ExpenseService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

/**
 * Implementation of {@link ExpenseResource}. Thin: forwards to
 * {@link ExpenseService}. The class-level {@code @Path} is repeated because
 * Quarkus REST registers the root resource from the concrete class.
 */
@Path("/expenses")
@ApplicationScoped
public class ExpenseResourceImpl implements ExpenseResource {

    @Inject
    ExpenseService expenseService;

    @Override
    public List<ExpenseDto> list(LocalDate from, LocalDate to) {
        return expenseService.list(from, to);
    }

    @Override
    public ExpenseDto detail(UUID id) {
        return expenseService.detail(id);
    }

    @Override
    public Response create(ExpenseDraftDto draft) {
        ExpenseDto created = expenseService.create(draft);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @Override
    public ExpenseDto update(UUID id, ExpenseDraftDto draft) {
        return expenseService.update(id, draft);
    }

    @Override
    public Response delete(UUID id) {
        expenseService.delete(id);
        return Response.noContent().build();
    }
}
