package com.pirris.resource;

import java.util.List;
import java.util.UUID;

import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestQuery;

import com.pirris.dto.service.ServiceRateDraftDto;
import com.pirris.dto.service.ServiceRateDto;
import com.pirris.dto.service.TrainingServiceDraftDto;
import com.pirris.dto.service.TrainingServiceDto;

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
 * Serves the Servizi screen: the catalogue of training services with their
 * rates, create/update and archive/restore of both. Implemented by
 * {@code ServiceCatalogResourceImpl}. Every endpoint is tenant-scoped through
 * the service layer and requires an authenticated account.
 */
@Path("/services")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({ "admin", "user" })
public interface ServiceCatalogResource {

    /**
     * Services with their rates, ordered by name.
     *
     * @param includeArchived when true, archived services and rates are included
     */
    @GET
    List<TrainingServiceDto> list(@RestQuery("includeArchived") boolean includeArchived);

    /** One service with all its rates. */
    @GET
    @Path("/{id}")
    TrainingServiceDto detail(@RestPath UUID id);

    /** Creates a service; responds 201 with the created service. */
    @POST
    Response create(@Valid TrainingServiceDraftDto draft);

    /** Updates an existing service. */
    @PUT
    @Path("/{id}")
    TrainingServiceDto update(@RestPath UUID id, @Valid TrainingServiceDraftDto draft);

    /** Archives a service (soft delete); responds 204. */
    @DELETE
    @Path("/{id}")
    Response archive(@RestPath UUID id);

    /** Restores an archived service; responds 204. */
    @POST
    @Path("/{id}/restore")
    Response restore(@RestPath UUID id);

    /** Adds a rate to the service; responds 201 with the created rate. */
    @POST
    @Path("/{id}/rates")
    Response addRate(@RestPath UUID id, @Valid ServiceRateDraftDto draft);

    /** Updates a rate of the service. */
    @PUT
    @Path("/{id}/rates/{rateId}")
    ServiceRateDto updateRate(@RestPath UUID id, @RestPath UUID rateId, @Valid ServiceRateDraftDto draft);

    /** Archives a rate (soft delete); responds 204. */
    @DELETE
    @Path("/{id}/rates/{rateId}")
    Response archiveRate(@RestPath UUID id, @RestPath UUID rateId);

    /** Restores an archived rate; responds 204. */
    @POST
    @Path("/{id}/rates/{rateId}/restore")
    Response restoreRate(@RestPath UUID id, @RestPath UUID rateId);
}
