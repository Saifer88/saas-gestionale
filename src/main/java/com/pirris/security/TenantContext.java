package com.pirris.security;

import java.util.Optional;
import java.util.UUID;

import org.eclipse.microprofile.jwt.JsonWebToken;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

/**
 * The current tenant (studio) for the active request, derived from the
 * authenticated JWT.
 *
 * <p>Deny by default: when no studio claim is present (unauthenticated context,
 * background work, tests without a token) {@link #currentStudioId()} throws, and
 * tenant-scoped data access via {@code TenantRepository} is refused rather than
 * silently operating across studios.</p>
 */
@RequestScoped
public class TenantContext {

    /** JWT claim carrying the studio (tenant) id. */
    public static final String STUDIO_CLAIM = "studio_id";

    @Inject
    JsonWebToken jwt;

    /** The current studio id, or empty when there is no authenticated tenant. */
    public Optional<UUID> studioId() {
        if (jwt == null) {
            return Optional.empty();
        }
        Object claim = jwt.getClaim(STUDIO_CLAIM);
        if (claim == null) {
            return Optional.empty();
        }
        String value = claim.toString().trim();
        if (value.isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    /** The current studio id or a failure when none is resolved (deny by default). */
    public UUID currentStudioId() {
        return studioId().orElseThrow(() -> new IllegalStateException(
                "Nessuno studio nel contesto: operazione non consentita senza tenant autenticato."));
    }
}
