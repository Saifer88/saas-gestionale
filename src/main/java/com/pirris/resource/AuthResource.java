package com.pirris.resource;

import com.pirris.dto.auth.ChangePasswordRequest;
import com.pirris.dto.auth.LoginRequest;
import com.pirris.dto.auth.TokenResponse;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Public authentication endpoints. Implemented by {@link AuthResourceImpl}.
 */
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AuthResource {

    /** Exchange credentials for a JWT. Public. */
    @POST
    @Path("/login")
    @PermitAll
    TokenResponse login(@Valid LoginRequest request);

    /** Change the authenticated account's own password. Any logged-in role. */
    @POST
    @Path("/change-password")
    @RolesAllowed({ "admin", "user" })
    Response changePassword(@Valid ChangePasswordRequest request);
}
