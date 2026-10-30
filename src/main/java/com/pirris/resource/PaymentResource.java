package com.pirris.resource;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestQuery;

import com.pirris.dto.payment.ClientBalanceDto;
import com.pirris.dto.payment.StatementDto;
import com.pirris.dto.payment.UnpaidLessonDto;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Serves the Pagamenti screen: derived client balances, unpaid lessons and
 * statements. Read-only. Implemented by {@code PaymentResourceImpl}. Every
 * endpoint is tenant-scoped through the service layer and requires an
 * authenticated account.
 */
@Path("/payments")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({ "admin", "user" })
public interface PaymentResource {

    /** Balances of all clients of the studio. */
    @GET
    @Path("/balances")
    List<ClientBalanceDto> balances();

    /** Balance of one client. */
    @GET
    @Path("/clients/{clientId}/balance")
    ClientBalanceDto balance(@RestPath UUID clientId);

    /** Unpaid lessons of one client. */
    @GET
    @Path("/clients/{clientId}/unpaid")
    List<UnpaidLessonDto> unpaidLessons(@RestPath UUID clientId);

    /**
     * Statement of one client.
     *
     * @param from optional first day (inclusive, ISO date)
     * @param to   optional last day (inclusive, ISO date)
     */
    @GET
    @Path("/clients/{clientId}/statement")
    StatementDto statement(@RestPath UUID clientId, @RestQuery("from") LocalDate from,
            @RestQuery("to") LocalDate to);
}
