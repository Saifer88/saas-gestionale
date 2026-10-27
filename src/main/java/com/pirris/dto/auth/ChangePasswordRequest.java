package com.pirris.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Payload for {@code POST /api/auth/change-password} (authenticated user). */
public record ChangePasswordRequest(
        @NotBlank String currentPassword,
        @NotBlank @Size(min = 8, message = "La nuova password deve avere almeno 8 caratteri")
        String newPassword) {
}
