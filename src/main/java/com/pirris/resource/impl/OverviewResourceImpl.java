package com.pirris.resource.impl;

import java.time.LocalDate;

import com.pirris.dto.overview.OverviewDto;
import com.pirris.resource.OverviewResource;
import com.pirris.service.OverviewService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;

/**
 * Implementation of {@link OverviewResource}. Thin: defaults the reference date
 * to the server date and forwards to {@link OverviewService}. The class-level
 * {@code @Path} is repeated because Quarkus REST registers the root resource
 * from the concrete class.
 */
@Path("/overview")
@ApplicationScoped
public class OverviewResourceImpl implements OverviewResource {

    @Inject
    OverviewService overviewService;

    @Override
    public OverviewDto overview(LocalDate reference) {
        return overviewService.overview(reference != null ? reference : LocalDate.now());
    }
}
