package com.pirris.resource.error;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Uniform error payload returned by the API for all handled failures.
 *
 * @param status    HTTP status code
 * @param error     short machine-ish label (e.g. {@code "Not Found"})
 * @param message   human-readable description
 * @param details   optional field-level validation messages
 * @param timestamp when the error was produced
 */
public record ApiError(int status, String error, String message, List<String> details,
        LocalDateTime timestamp) {

    public static ApiError of(int status, String error, String message, List<String> details) {
        return new ApiError(status, error, message,
                details == null ? List.of() : details, LocalDateTime.now());
    }
}
