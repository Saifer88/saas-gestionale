package com.pirris.dto.auth;

import jakarta.validation.constraints.NotBlank;

/** Credentials submitted to {@code POST /api/auth/login}. */
public record LoginRequest(
        @NotBlank String email,
        @NotBlank String password) {
}
