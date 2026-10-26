package com.pirris.resource;

import java.time.LocalDate;

import org.jboss.resteasy.reactive.RestQuery;

import com.pirris.dto.overview.OverviewDto;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Serves the Panoramica (overview) screen. Implemented by
 * {@link OverviewResourceImpl}.
 */
@Path("/overview")
@Produces(MediaType.APPLICATION_JSON)
public interface OverviewResource {

    /**
     * Full overview payload.
     *
     * @param reference optional "today" (ISO date); defaults to the server date
     *                  when absent. Lets the client ask for its own local day.
     */
    @GET
    OverviewDto overview(@RestQuery LocalDate reference);
}
