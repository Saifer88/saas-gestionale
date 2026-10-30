package com.pirris.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.pirris.dto.service.ServiceRateDraftDto;
import com.pirris.dto.service.ServiceRateDto;
import com.pirris.dto.service.TrainingServiceDraftDto;
import com.pirris.dto.service.TrainingServiceDto;
import com.pirris.mapper.ServiceCatalogMapper;
import com.pirris.model.ServiceRateModel;
import com.pirris.model.TrainingServiceModel;
import com.pirris.repository.ServiceRateRepository;
import com.pirris.repository.TrainingServiceRepository;
import com.pirris.resource.error.NotFoundException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Business logic for the Servizi view: the catalogue of training services and
 * their rates. All data access goes through tenant-aware repositories.
 * Services and rates are archived, never physically deleted.
 *
 * <p>Reads are implemented; writes are stubs until the write flow is built.</p>
 */
@ApplicationScoped
public class ServiceCatalogService {

    @Inject
    TrainingServiceRepository services;

    @Inject
    ServiceRateRepository rates;

    @Inject
    ServiceCatalogMapper mapper;

    /** Services of the studio with their rates; archived ones only on request. */
    public List<TrainingServiceDto> list(boolean includeArchived) {
        List<TrainingServiceModel> all = services.list(includeArchived);
        List<UUID> ids = all.stream().map(s -> s.id).toList();
        Map<UUID, List<ServiceRateModel>> byService = rates.listByServices(ids, includeArchived)
                .stream()
                .collect(Collectors.groupingBy(r -> r.serviceId));
        return all.stream()
                .map(s -> mapper.toDto(s, mapper.toRateDtos(byService.getOrDefault(s.id, List.of()))))
                .toList();
    }

    /** One service with all its rates (archived included); 404 when not in the studio. */
    public TrainingServiceDto detail(UUID id) {
        TrainingServiceModel service = requireService(id);
        return mapper.toDto(service, mapper.toRateDtos(rates.listByServices(List.of(id), true)));
    }

    /** Creates a service from the draft. */
    public TrainingServiceDto create(TrainingServiceDraftDto draft) {
        // TODO: map the draft onto a new TrainingServiceModel and persist it for the tenant.
        throw new UnsupportedOperationException("ServiceCatalogService.create not yet implemented");
    }

    /** Updates an existing service from the draft. */
    public TrainingServiceDto update(UUID id, TrainingServiceDraftDto draft) {
        // TODO: require the service in the studio, apply the draft.
        throw new UnsupportedOperationException("ServiceCatalogService.update not yet implemented");
    }

    /** Archives a service (soft delete). */
    public void archive(UUID id) {
        // TODO: require the service in the studio, set archived = true.
        throw new UnsupportedOperationException("ServiceCatalogService.archive not yet implemented");
    }

    /** Restores an archived service. */
    public void restore(UUID id) {
        // TODO: require the service in the studio, set archived = false.
        throw new UnsupportedOperationException("ServiceCatalogService.restore not yet implemented");
    }

    /** Adds a rate to a service. */
    public ServiceRateDto addRate(UUID serviceId, ServiceRateDraftDto draft) {
        // TODO: require the service in the studio, persist a new ServiceRateModel for it.
        throw new UnsupportedOperationException("ServiceCatalogService.addRate not yet implemented");
    }

    /** Updates a rate of a service. */
    public ServiceRateDto updateRate(UUID serviceId, UUID rateId, ServiceRateDraftDto draft) {
        // TODO: require the rate in the studio and belonging to serviceId, apply the draft.
        throw new UnsupportedOperationException("ServiceCatalogService.updateRate not yet implemented");
    }

    /** Archives a rate (soft delete). */
    public void archiveRate(UUID serviceId, UUID rateId) {
        // TODO: require the rate in the studio and belonging to serviceId, set archived = true.
        throw new UnsupportedOperationException("ServiceCatalogService.archiveRate not yet implemented");
    }

    /** Restores an archived rate. */
    public void restoreRate(UUID serviceId, UUID rateId) {
        // TODO: require the rate in the studio and belonging to serviceId, set archived = false.
        throw new UnsupportedOperationException("ServiceCatalogService.restoreRate not yet implemented");
    }

    private TrainingServiceModel requireService(UUID id) {
        return services.findByIdForTenant(id)
                .orElseThrow(() -> NotFoundException.of("TrainingService", id));
    }
}
