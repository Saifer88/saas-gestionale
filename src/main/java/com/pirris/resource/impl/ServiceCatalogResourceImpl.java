package com.pirris.resource.impl;

import java.util.List;
import java.util.UUID;

import com.pirris.dto.service.ServiceRateDraftDto;
import com.pirris.dto.service.ServiceRateDto;
import com.pirris.dto.service.TrainingServiceDraftDto;
import com.pirris.dto.service.TrainingServiceDto;
import com.pirris.resource.ServiceCatalogResource;
import com.pirris.service.ServiceCatalogService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

/**
 * Implementation of {@link ServiceCatalogResource}. Thin: forwards to
 * {@link ServiceCatalogService}. The class-level {@code @Path} is repeated
 * because Quarkus REST registers the root resource from the concrete class.
 */
@Path("/services")
@ApplicationScoped
public class ServiceCatalogResourceImpl implements ServiceCatalogResource {

    @Inject
    ServiceCatalogService catalogService;

    @Override
    public List<TrainingServiceDto> list(boolean includeArchived) {
        return catalogService.list(includeArchived);
    }

    @Override
    public TrainingServiceDto detail(UUID id) {
        return catalogService.detail(id);
    }

    @Override
    public Response create(TrainingServiceDraftDto draft) {
        TrainingServiceDto created = catalogService.create(draft);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @Override
    public TrainingServiceDto update(UUID id, TrainingServiceDraftDto draft) {
        return catalogService.update(id, draft);
    }

    @Override
    public Response archive(UUID id) {
        catalogService.archive(id);
        return Response.noContent().build();
    }

    @Override
    public Response restore(UUID id) {
        catalogService.restore(id);
        return Response.noContent().build();
    }

    @Override
    public Response addRate(UUID id, ServiceRateDraftDto draft) {
        ServiceRateDto created = catalogService.addRate(id, draft);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @Override
    public ServiceRateDto updateRate(UUID id, UUID rateId, ServiceRateDraftDto draft) {
        return catalogService.updateRate(id, rateId, draft);
    }

    @Override
    public Response archiveRate(UUID id, UUID rateId) {
        catalogService.archiveRate(id, rateId);
        return Response.noContent().build();
    }

    @Override
    public Response restoreRate(UUID id, UUID rateId) {
        catalogService.restoreRate(id, rateId);
        return Response.noContent().build();
    }
}
