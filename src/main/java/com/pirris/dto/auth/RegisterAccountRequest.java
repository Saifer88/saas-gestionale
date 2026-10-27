package com.pirris.dto.auth;

import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Admin-only payload to provision access ({@code POST /api/admin/accounts}).
 *
 * <p>If {@code studioId} is null a new studio named {@code studioName} is created
 * and the account becomes its first member; otherwise the account is added to the
 * existing studio.</p>
 */
public record RegisterAccountRequest(
        UUID studioId,
        String studioName,
        @Email @NotBlank String email,
        @NotBlank @Size(min = 8) String password,
        @NotBlank String displayName,
        /** "admin" or "user"; defaults to "user" when blank. */
        String role) {
}
