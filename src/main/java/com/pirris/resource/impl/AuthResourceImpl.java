package com.pirris.resource.impl;

import java.util.UUID;

import org.eclipse.microprofile.jwt.JsonWebToken;

import com.pirris.dto.auth.ChangePasswordRequest;
import com.pirris.dto.auth.LoginRequest;
import com.pirris.dto.auth.TokenResponse;
import com.pirris.resource.AuthResource;
import com.pirris.service.AuthService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

/**
 * Implementation of {@link AuthResource}.
 *
 * <p>Quarkus REST registers a root resource from the concrete class, so the
 * {@code @Path} is repeated here; the per-method annotations and role rules live
 * on the interface.</p>
 */
@Path("/auth")
@ApplicationScoped
public class AuthResourceImpl implements AuthResource {

    @Inject
    AuthService authService;

    @Inject
    JsonWebToken jwt;

    @Override
    public TokenResponse login(LoginRequest request) {
        return authService.login(request);
    }

    @Override
    public Response changePassword(ChangePasswordRequest request) {
        UUID accountId = UUID.fromString(jwt.getSubject());
        authService.changePassword(accountId, request);
        return Response.noContent().build();
    }
}
