package com.pirris.service;

import java.time.LocalDate;

import com.pirris.dto.overview.OverviewDto;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Builds the data for the Panoramica (overview) screen.
 *
 * <p><b>Stub.</b> The real implementation will aggregate ledger entries and
 * planned sessions per the frontend's rules (see PROGETTO.md §4.6): gross income
 * for current calendar periods, forecast = sum of prices of still-planned
 * lessons in the future excluding package-covered participants, weekly counters
 * where a client booked multiple times counts once and a group lesson counts as
 * one lesson.</p>
 */
@ApplicationScoped
public class OverviewService {

    /**
     * @param reference the "today" the overview is computed against (the client's
     *                  local date); the week is Monday–Sunday containing it.
     */
    public OverviewDto overview(LocalDate reference) {
        // TODO: aggregate income counters, weekly counters and today's appointments.
        throw new UnsupportedOperationException("OverviewService.overview not yet implemented");
    }
}
