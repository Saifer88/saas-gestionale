package com.pirris.resource;

import java.util.List;
import java.util.UUID;

import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestQuery;

import com.pirris.dto.ClientDraftDto;
import com.pirris.dto.client.ClientDetailDto;
import com.pirris.dto.client.ClientSummaryDto;

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
 * Serves the Clienti screen: list, search, detail, create/update and archive.
 * Implemented by {@code ClientResourceImpl}. Every endpoint is tenant-scoped
 * through the service layer and requires an authenticated account.
 */
@Path("/clients")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({ "admin", "user" })
public interface ClientResource {

    /**
     * Active clients, or search results when {@code q} is present.
     *
     * @param q optional case-insensitive term matched over name, phone and email
     */
    @GET
    List<ClientSummaryDto> list(@RestQuery("q") String q);

    /** Full profile of one client. */
    @GET
    @Path("/{id}")
    ClientDetailDto detail(@RestPath UUID id);

    /** Creates a client from the draft; responds 201 with the created profile. */
    @POST
    Response create(@Valid ClientDraftDto draft);

    /** Updates an existing client from the draft. */
    @PUT
    @Path("/{id}")
    ClientDetailDto update(@RestPath UUID id, @Valid ClientDraftDto draft);

    /** Archives a client (soft delete); responds 204. */
    @DELETE
    @Path("/{id}")
    Response archive(@RestPath UUID id);

    /** Restores an archived client; responds 204. */
    @POST
    @Path("/{id}/restore")
    Response restore(@RestPath UUID id);
}
