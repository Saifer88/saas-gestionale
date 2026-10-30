package com.pirris.resource.impl;

import java.util.List;
import java.util.UUID;

import com.pirris.dto.course.CourseDetailDto;
import com.pirris.dto.course.CourseDraftDto;
import com.pirris.dto.course.CourseEnrollmentDraftDto;
import com.pirris.dto.course.CourseParticipantDto;
import com.pirris.dto.course.CourseSummaryDto;
import com.pirris.resource.CourseResource;
import com.pirris.service.CourseService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

/**
 * Implementation of {@link CourseResource}. Thin: forwards to
 * {@link CourseService}. The class-level {@code @Path} is repeated because
 * Quarkus REST registers the root resource from the concrete class.
 */
@Path("/courses")
@ApplicationScoped
public class CourseResourceImpl implements CourseResource {

    @Inject
    CourseService courseService;

    @Override
    public List<CourseSummaryDto> list(boolean includeInactive) {
        return courseService.list(includeInactive);
    }

    @Override
    public CourseDetailDto detail(UUID id) {
        return courseService.detail(id);
    }

    @Override
    public Response create(CourseDraftDto draft) {
        CourseDetailDto created = courseService.create(draft);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @Override
    public CourseDetailDto update(UUID id, CourseDraftDto draft) {
        return courseService.update(id, draft);
    }

    @Override
    public Response deactivate(UUID id) {
        courseService.deactivate(id);
        return Response.noContent().build();
    }

    @Override
    public Response activate(UUID id) {
        courseService.activate(id);
        return Response.noContent().build();
    }

    @Override
    public Response enroll(UUID id, CourseEnrollmentDraftDto draft) {
        CourseParticipantDto created = courseService.enroll(id, draft);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @Override
    public Response unenroll(UUID id, UUID participantId) {
        courseService.unenroll(id, participantId);
        return Response.noContent().build();
    }
}
