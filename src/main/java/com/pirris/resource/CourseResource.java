package com.pirris.resource;

import java.util.List;
import java.util.UUID;

import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestQuery;

import com.pirris.dto.course.CourseDetailDto;
import com.pirris.dto.course.CourseDraftDto;
import com.pirris.dto.course.CourseEnrollmentDraftDto;
import com.pirris.dto.course.CourseSummaryDto;

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
 * Serves the Corsi screen: course templates (CRUD, deactivate/reactivate) and
 * enrollments. Implemented by {@code CourseResourceImpl}. Every endpoint is
 * tenant-scoped through the service layer and requires an authenticated
 * account.
 */
@Path("/courses")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({ "admin", "user" })
public interface CourseResource {

    /**
     * Courses by weekday and time.
     *
     * @param includeInactive when true, deactivated courses are included
     */
    @GET
    List<CourseSummaryDto> list(@RestQuery("includeInactive") boolean includeInactive);

    /** One course with its active enrollments. */
    @GET
    @Path("/{id}")
    CourseDetailDto detail(@RestPath UUID id);

    /** Creates a course; responds 201 with the created course. */
    @POST
    Response create(@Valid CourseDraftDto draft);

    /** Updates an existing course. */
    @PUT
    @Path("/{id}")
    CourseDetailDto update(@RestPath UUID id, @Valid CourseDraftDto draft);

    /** Deactivates a course (soft); responds 204. */
    @DELETE
    @Path("/{id}")
    Response deactivate(@RestPath UUID id);

    /** Reactivates a course; responds 204. */
    @POST
    @Path("/{id}/restore")
    Response activate(@RestPath UUID id);

    /** Enrolls a client; responds 201 with the created enrollment. */
    @POST
    @Path("/{id}/participants")
    Response enroll(@RestPath UUID id, @Valid CourseEnrollmentDraftDto draft);

    /** Unenrolls a participant (soft); responds 204. */
    @DELETE
    @Path("/{id}/participants/{participantId}")
    Response unenroll(@RestPath UUID id, @RestPath UUID participantId);
}
