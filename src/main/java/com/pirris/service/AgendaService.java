package com.pirris.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.pirris.dto.agenda.CalendarSlotDto;
import com.pirris.dto.agenda.CalendarView;
import com.pirris.dto.agenda.SessionDetailDto;
import com.pirris.dto.agenda.SessionDraftDto;
import com.pirris.dto.agenda.SessionParticipantDraftDto;
import com.pirris.dto.agenda.SessionParticipantDto;
import com.pirris.dto.agenda.UnavailabilityDraftDto;
import com.pirris.dto.agenda.UnavailabilityDto;
import com.pirris.mapper.AgendaMapper;
import com.pirris.model.TrainingSessionModel;
import com.pirris.model.UnavailabilityModel;
import com.pirris.repository.SessionParticipantRepository;
import com.pirris.repository.TrainingSessionRepository;
import com.pirris.repository.UnavailabilityRepository;
import com.pirris.resource.error.NotFoundException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Business logic for the Agenda view: the day/week/month calendar, sessions
 * with their participants and the trainer's unavailability blocks. Sessions and
 * unavailability stay separate entities, merged only into
 * {@link CalendarSlotDto} for the calendar. All data access goes through
 * tenant-aware repositories.
 *
 * <p>Reads are implemented; writes are stubs. Overlap validation and slot
 * suggestion belong to the Phase 3 {@code SchedulingService}.</p>
 */
@ApplicationScoped
public class AgendaService {

    @Inject
    TrainingSessionRepository sessions;

    @Inject
    SessionParticipantRepository participants;

    @Inject
    UnavailabilityRepository unavailabilities;

    @Inject
    AgendaMapper mapper;

    /**
     * Calendar blocks of the range containing {@code reference}: non-cancelled
     * sessions starting in the range plus unavailability blocks overlapping it,
     * ordered by start.
     *
     * @param view      DAY, WEEK (Monday–Sunday) or MONTH
     * @param reference any date inside the wanted range
     */
    public List<CalendarSlotDto> calendar(CalendarView view, LocalDate reference) {
        LocalDate first = switch (view) {
            case DAY -> reference;
            case WEEK -> reference.with(DayOfWeek.MONDAY);
            case MONTH -> reference.withDayOfMonth(1);
        };
        LocalDate afterLast = switch (view) {
            case DAY -> first.plusDays(1);
            case WEEK -> first.plusDays(7);
            case MONTH -> first.plusMonths(1);
        };
        LocalDateTime start = first.atStartOfDay();
        LocalDateTime end = afterLast.atStartOfDay();

        List<TrainingSessionModel> inRange = sessions.listNotCancelledBetween(start, end);
        List<UUID> sessionIds = inRange.stream().map(s -> s.id).toList();
        Map<UUID, List<UUID>> clientIdsBySession = participants.listBySessions(sessionIds).stream()
                .collect(Collectors.groupingBy(p -> p.sessionId,
                        Collectors.mapping(p -> p.clientId, Collectors.toList())));

        List<CalendarSlotDto> slots = new ArrayList<>();
        for (TrainingSessionModel session : inRange) {
            slots.add(mapper.sessionSlot(session, clientIdsBySession.getOrDefault(session.id, List.of())));
        }
        for (UnavailabilityModel block : unavailabilities.listOverlapping(start, end)) {
            slots.add(mapper.unavailabilitySlot(block));
        }
        slots.sort(Comparator.comparing(CalendarSlotDto::start));
        return slots;
    }

    // ---- sessions ----

    /** One session with its participants; 404 when not in the studio. */
    public SessionDetailDto sessionDetail(UUID id) {
        TrainingSessionModel session = requireSession(id);
        return mapper.toSessionDetail(session, mapper.toParticipantDtos(participants.listBySession(session.id)));
    }

    /** Creates a session with its participants. */
    public SessionDetailDto createSession(SessionDraftDto draft) {
        // TODO(Phase 3): overlap validation via SchedulingService, tenant-check referenced ids,
        // persist session + participants, client preference snapshot.
        throw new UnsupportedOperationException("AgendaService.createSession not yet implemented");
    }

    /** Updates a session and its participants. */
    public SessionDetailDto updateSession(UUID id, SessionDraftDto draft) {
        // TODO(Phase 3): overlap validation via SchedulingService, tenant-check referenced ids,
        // apply the draft, client preference snapshot.
        throw new UnsupportedOperationException("AgendaService.updateSession not yet implemented");
    }

    /** Deletes a session and its participants. */
    public void deleteSession(UUID id) {
        // TODO(Phase 3): decide physical delete vs status CANCELLED; remove participants.
        throw new UnsupportedOperationException("AgendaService.deleteSession not yet implemented");
    }

    /** Adds a participant to a session. */
    public SessionParticipantDto addParticipant(UUID sessionId, SessionParticipantDraftDto draft) {
        // TODO: require the session in the studio, tenant-check client/package, persist.
        throw new UnsupportedOperationException("AgendaService.addParticipant not yet implemented");
    }

    /** Updates a participant of a session. */
    public SessionParticipantDto updateParticipant(UUID sessionId, UUID participantId,
            SessionParticipantDraftDto draft) {
        // TODO: require the participant in the studio and belonging to sessionId, apply the draft.
        throw new UnsupportedOperationException("AgendaService.updateParticipant not yet implemented");
    }

    /** Removes a participant from a session. */
    public void removeParticipant(UUID sessionId, UUID participantId) {
        // TODO: require the participant in the studio and belonging to sessionId, delete it.
        throw new UnsupportedOperationException("AgendaService.removeParticipant not yet implemented");
    }

    // ---- unavailability ----

    /** One unavailability block; 404 when not in the studio. */
    public UnavailabilityDto unavailabilityDetail(UUID id) {
        return mapper.toUnavailabilityDto(requireUnavailability(id));
    }

    /** Creates an unavailability block. */
    public UnavailabilityDto createUnavailability(UnavailabilityDraftDto draft) {
        // TODO(Phase 3): validate start < end and overlaps via SchedulingService, persist.
        throw new UnsupportedOperationException("AgendaService.createUnavailability not yet implemented");
    }

    /** Updates an unavailability block. */
    public UnavailabilityDto updateUnavailability(UUID id, UnavailabilityDraftDto draft) {
        // TODO(Phase 3): require the block in the studio, validate, apply the draft.
        throw new UnsupportedOperationException("AgendaService.updateUnavailability not yet implemented");
    }

    /** Deletes an unavailability block (physical: the table has no archived flag). */
    public void deleteUnavailability(UUID id) {
        // TODO: deleteByIdForTenant, 404 when nothing was removed.
        throw new UnsupportedOperationException("AgendaService.deleteUnavailability not yet implemented");
    }

    private TrainingSessionModel requireSession(UUID id) {
        return sessions.findByIdForTenant(id)
                .orElseThrow(() -> NotFoundException.of("TrainingSession", id));
    }

    private UnavailabilityModel requireUnavailability(UUID id) {
        return unavailabilities.findByIdForTenant(id)
                .orElseThrow(() -> NotFoundException.of("Unavailability", id));
    }
}
