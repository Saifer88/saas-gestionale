package com.pirris.dto.overview;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import com.pirris.model.enums.SessionStatus;

/**
 * Payload for the Panoramica (overview) screen. Mirrors the four rows the app
 * shows, in order:
 * <ol>
 *   <li>income counters (week / month / year / forecast),</li>
 *   <li>weekly booked-clients and planned-lessons counters,</li>
 *   <li>today's appointments (cancelled excluded),</li>
 *   <li>shortcuts are a pure UI concern and not part of this payload.</li>
 * </ol>
 * All amounts are in cents.
 */
public record OverviewDto(
        IncomeCounters income,
        WeeklyCounters week,
        List<TodayAppointment> todayAppointments) {

    /** Row 1: gross income for the current calendar periods plus forecast. */
    public record IncomeCounters(
            long weekCents,
            long monthCents,
            long yearCents,
            long forecastCents) {
    }

    /** Row 2: a client booked several times counts once; a group lesson counts once. */
    public record WeeklyCounters(
            int bookedClients,
            int plannedLessons) {
    }

    /**
     * Row 3: one entry per appointment today, ordered by time. Participants are
     * referenced by id (names are not denormalized; the app resolves them from
     * the client list, as for agenda slots).
     */
    public record TodayAppointment(
            UUID sessionId,
            LocalTime time,
            List<UUID> clientIds,
            long totalPriceCents,
            SessionStatus status) {
    }
}
