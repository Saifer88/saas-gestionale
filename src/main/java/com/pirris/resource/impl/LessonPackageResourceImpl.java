package com.pirris.resource.impl;

import java.util.List;
import java.util.UUID;

import com.pirris.dto.lessonpackage.LessonPackageDto;
import com.pirris.dto.lessonpackage.PackagePurchaseDraftDto;
import com.pirris.resource.LessonPackageResource;
import com.pirris.service.LessonPackageService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

/**
 * Implementation of {@link LessonPackageResource}. Thin: forwards to
 * {@link LessonPackageService}. The class-level {@code @Path} is repeated
 * because Quarkus REST registers the root resource from the concrete class.
 */
@Path("/packages")
@ApplicationScoped
public class LessonPackageResourceImpl implements LessonPackageResource {

    @Inject
    LessonPackageService packageService;

    @Override
    public List<LessonPackageDto> list(UUID clientId) {
        return packageService.list(clientId);
    }

    @Override
    public LessonPackageDto detail(UUID id) {
        return packageService.detail(id);
    }

    @Override
    public Response purchase(PackagePurchaseDraftDto draft) {
        LessonPackageDto created = packageService.purchase(draft);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }
}
