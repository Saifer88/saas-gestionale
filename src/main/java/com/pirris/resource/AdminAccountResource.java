package com.pirris.resource;

import com.pirris.dto.auth.RegisterAccountRequest;
import com.pirris.dto.auth.TokenResponse;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Admin-only account provisioning. Implemented by {@link AdminAccountResourceImpl}.
 */
@Path("/admin/accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("admin")
public interface AdminAccountResource {

    /** Create a studio (if needed) and an account within it. */
    @POST
    Response register(@Valid RegisterAccountRequest request);
}
