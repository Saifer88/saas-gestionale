package com.pirris.resource;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestQuery;

import com.pirris.dto.agenda.CalendarSlotDto;
import com.pirris.dto.agenda.CalendarView;
import com.pirris.dto.agenda.SessionDetailDto;
import com.pirris.dto.agenda.SessionDraftDto;
import com.pirris.dto.agenda.SessionParticipantDraftDto;
import com.pirris.dto.agenda.SessionParticipantDto;
import com.pirris.dto.agenda.UnavailabilityDraftDto;
import com.pirris.dto.agenda.UnavailabilityDto;

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
 * Serves the Agenda screen: the day/week/month calendar (sessions and
 * unavailability merged into common slots), sessions with participants and
 * unavailability blocks. Implemented by {@code AgendaResourceImpl}. Every
 * endpoint is tenant-scoped through the service layer and requires an
 * authenticated account.
 */
@Path("/agenda")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({ "admin", "user" })
public interface AgendaResource {

    /**
     * Calendar slots of the range containing {@code reference}, ordered by start.
     *
     * @param view      optional DAY | WEEK | MONTH; defaults to WEEK (Monday–Sunday)
     * @param reference optional ISO date inside the range; defaults to the server date
     */
    @GET
    List<CalendarSlotDto> calendar(@RestQuery("view") CalendarView view,
            @RestQuery("reference") LocalDate reference);

    // ---- sessions ----

    /** One session with its participants. */
    @GET
    @Path("/sessions/{id}")
    SessionDetailDto sessionDetail(@RestPath UUID id);

    /** Creates a session with participants; responds 201 with the created session. */
    @POST
    @Path("/sessions")
    Response createSession(@Valid SessionDraftDto draft);

    /** Updates a session and its participants. */
    @PUT
    @Path("/sessions/{id}")
    SessionDetailDto updateSession(@RestPath UUID id, @Valid SessionDraftDto draft);

    /** Deletes a session; responds 204. */
    @DELETE
    @Path("/sessions/{id}")
    Response deleteSession(@RestPath UUID id);

    /** Adds a participant; responds 201 with the created participant. */
    @POST
    @Path("/sessions/{id}/participants")
    Response addParticipant(@RestPath UUID id, @Valid SessionParticipantDraftDto draft);

    /** Updates a participant of the session. */
    @PUT
    @Path("/sessions/{id}/participants/{participantId}")
    SessionParticipantDto updateParticipant(@RestPath UUID id, @RestPath UUID participantId,
            @Valid SessionParticipantDraftDto draft);

    /** Removes a participant from the session; responds 204. */
    @DELETE
    @Path("/sessions/{id}/participants/{participantId}")
    Response removeParticipant(@RestPath UUID id, @RestPath UUID participantId);

    // ---- unavailability ----

    /** One unavailability block. */
    @GET
    @Path("/unavailabilities/{id}")
    UnavailabilityDto unavailabilityDetail(@RestPath UUID id);

    /** Creates an unavailability block; responds 201 with the created block. */
    @POST
    @Path("/unavailabilities")
    Response createUnavailability(@Valid UnavailabilityDraftDto draft);

    /** Updates an unavailability block. */
    @PUT
    @Path("/unavailabilities/{id}")
    UnavailabilityDto updateUnavailability(@RestPath UUID id, @Valid UnavailabilityDraftDto draft);

    /** Deletes an unavailability block; responds 204. */
    @DELETE
    @Path("/unavailabilities/{id}")
    Response deleteUnavailability(@RestPath UUID id);
}
