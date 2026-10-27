package com.pirris.resource.impl;

import com.pirris.dto.auth.RegisterAccountRequest;
import com.pirris.dto.auth.TokenResponse;
import com.pirris.resource.AdminAccountResource;
import com.pirris.service.AuthService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

/**
 * Implementation of {@link AdminAccountResource}.
 *
 * <p>Quarkus REST registers a root resource from the concrete class, so the
 * {@code @Path} is repeated here; role rules live on the interface.</p>
 */
@Path("/admin/accounts")
@ApplicationScoped
public class AdminAccountResourceImpl implements AdminAccountResource {

    @Inject
    AuthService authService;

    @Override
    public Response register(RegisterAccountRequest request) {
        TokenResponse created = authService.register(request);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }
}
