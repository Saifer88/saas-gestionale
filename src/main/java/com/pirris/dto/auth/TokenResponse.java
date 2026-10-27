package com.pirris.dto.auth;

/**
 * Issued access token and the essentials the client needs after login.
 *
 * @param accessToken  signed JWT (Bearer)
 * @param expiresInSeconds token lifetime
 * @param studioId     tenant the token is scoped to
 * @param role         role of the authenticated account ("admin"/"user")
 * @param displayName  human-readable account name
 */
public record TokenResponse(
        String accessToken,
        long expiresInSeconds,
        String studioId,
        String role,
        String displayName) {
}
