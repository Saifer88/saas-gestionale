package com.pirris.resource.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.pirris.dto.agenda.CalendarSlotDto;
import com.pirris.dto.agenda.CalendarView;
import com.pirris.dto.agenda.SessionDetailDto;
import com.pirris.dto.agenda.SessionDraftDto;
import com.pirris.dto.agenda.SessionParticipantDraftDto;
import com.pirris.dto.agenda.SessionParticipantDto;
import com.pirris.dto.agenda.UnavailabilityDraftDto;
import com.pirris.dto.agenda.UnavailabilityDto;
import com.pirris.resource.AgendaResource;
import com.pirris.service.AgendaService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

/**
 * Implementation of {@link AgendaResource}. Thin: applies the calendar
 * defaults and forwards to {@link AgendaService}. The class-level {@code @Path}
 * is repeated because Quarkus REST registers the root resource from the
 * concrete class.
 */
@Path("/agenda")
@ApplicationScoped
public class AgendaResourceImpl implements AgendaResource {

    @Inject
    AgendaService agendaService;

    @Override
    public List<CalendarSlotDto> calendar(CalendarView view, LocalDate reference) {
        return agendaService.calendar(
                view != null ? view : CalendarView.WEEK,
                reference != null ? reference : LocalDate.now());
    }

    // ---- sessions ----

    @Override
    public SessionDetailDto sessionDetail(UUID id) {
        return agendaService.sessionDetail(id);
    }

    @Override
    public Response createSession(SessionDraftDto draft) {
        SessionDetailDto created = agendaService.createSession(draft);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @Override
    public SessionDetailDto updateSession(UUID id, SessionDraftDto draft) {
        return agendaService.updateSession(id, draft);
    }

    @Override
    public Response deleteSession(UUID id) {
        agendaService.deleteSession(id);
        return Response.noContent().build();
    }

    @Override
    public Response addParticipant(UUID id, SessionParticipantDraftDto draft) {
        SessionParticipantDto created = agendaService.addParticipant(id, draft);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @Override
    public SessionParticipantDto updateParticipant(UUID id, UUID participantId,
            SessionParticipantDraftDto draft) {
        return agendaService.updateParticipant(id, participantId, draft);
    }

    @Override
    public Response removeParticipant(UUID id, UUID participantId) {
        agendaService.removeParticipant(id, participantId);
        return Response.noContent().build();
    }

    // ---- unavailability ----

    @Override
    public UnavailabilityDto unavailabilityDetail(UUID id) {
        return agendaService.unavailabilityDetail(id);
    }

    @Override
    public Response createUnavailability(UnavailabilityDraftDto draft) {
        UnavailabilityDto created = agendaService.createUnavailability(draft);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @Override
    public UnavailabilityDto updateUnavailability(UUID id, UnavailabilityDraftDto draft) {
        return agendaService.updateUnavailability(id, draft);
    }

    @Override
    public Response deleteUnavailability(UUID id) {
        agendaService.deleteUnavailability(id);
        return Response.noContent().build();
    }
}
