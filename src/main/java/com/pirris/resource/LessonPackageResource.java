package com.pirris.resource;

import java.util.List;
import java.util.UUID;

import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestQuery;

import com.pirris.dto.lessonpackage.LessonPackageDto;
import com.pirris.dto.lessonpackage.PackagePurchaseDraftDto;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Serves the Pacchetti screen: packages per client, detail and purchase.
 * Implemented by {@code LessonPackageResourceImpl}. Every endpoint is
 * tenant-scoped through the service layer and requires an authenticated
 * account.
 */
@Path("/packages")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({ "admin", "user" })
public interface LessonPackageResource {

    /**
     * Packages, most recent purchase first.
     *
     * @param clientId optional client filter; 404 when the client is not in the studio
     */
    @GET
    List<LessonPackageDto> list(@RestQuery("clientId") UUID clientId);

    /** One package. */
    @GET
    @Path("/{id}")
    LessonPackageDto detail(@RestPath UUID id);

    /** Purchases a package; responds 201 with the created package. */
    @POST
    Response purchase(@Valid PackagePurchaseDraftDto draft);
}
