package com.pirris.resource;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestQuery;

import com.pirris.dto.expense.ExpenseDraftDto;
import com.pirris.dto.expense.ExpenseDto;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Serves the Spese screen: list, detail, create/update and delete expenses.
 * Implemented by {@code ExpenseResourceImpl}. Every endpoint is tenant-scoped
 * through the service layer and requires an authenticated account.
 */
@Path("/expenses")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({ "admin", "user" })
public interface ExpenseResource {

    /**
     * Expenses, newest first.
     *
     * @param from optional first day (inclusive, ISO date)
     * @param to   optional last day (inclusive, ISO date)
     */
    @GET
    List<ExpenseDto> list(@RestQuery("from") LocalDate from, @RestQuery("to") LocalDate to);

    /** One expense. */
    @GET
    @Path("/{id}")
    ExpenseDto detail(@RestPath UUID id);

    /** Creates an expense; responds 201 with the created expense. */
    @POST
    Response create(@Valid ExpenseDraftDto draft);

    /** Updates an existing expense. */
    @PUT
    @Path("/{id}")
    ExpenseDto update(@RestPath UUID id, @Valid ExpenseDraftDto draft);

    /** Deletes an expense; responds 204. */
    @DELETE
    @Path("/{id}")
    Response delete(@RestPath UUID id);
}
