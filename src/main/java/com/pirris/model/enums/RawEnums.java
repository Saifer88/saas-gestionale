package com.pirris.model.enums;

import java.util.function.Function;

/**
 * Small helper for enums that are persisted through a lowercase/camelCase
 * {@code *_raw} string column instead of the Java constant name.
 */
final class RawEnums {

    private RawEnums() {
    }

    /**
     * Resolves an enum constant from its raw persisted value, case-insensitively.
     *
     * @param values      all constants of the enum
     * @param rawAccessor function returning the raw value of a constant
     * @param value       the persisted raw value (may be {@code null}/blank)
     * @param fallback    constant to return when {@code value} is unknown or blank
     */
    static <E extends Enum<E>> E fromRaw(E[] values, Function<E, String> rawAccessor,
            String value, E fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        String trimmed = value.trim();
        for (E candidate : values) {
            if (rawAccessor.apply(candidate).equalsIgnoreCase(trimmed)
                    || candidate.name().equalsIgnoreCase(trimmed)) {
                return candidate;
            }
        }
        return fallback;
    }

    /**
     * Strict variant used when deserializing JSON: resolves a constant from its
     * raw token (or constant name), case-insensitively, and rejects unknown or
     * blank values instead of silently falling back. Jackson turns the thrown
     * {@link IllegalArgumentException} into a 400 response.
     *
     * @param type        enum class, used in the error message
     * @param values      all constants of the enum
     * @param rawAccessor function returning the raw value of a constant
     * @param value       the incoming raw token
     */
    static <E extends Enum<E>> E parse(Class<E> type, E[] values, Function<E, String> rawAccessor,
            String value) {
        if (value != null && !value.isBlank()) {
            String trimmed = value.trim();
            for (E candidate : values) {
                if (rawAccessor.apply(candidate).equalsIgnoreCase(trimmed)
                        || candidate.name().equalsIgnoreCase(trimmed)) {
                    return candidate;
                }
            }
        }
        throw new IllegalArgumentException(
                "Unknown " + type.getSimpleName() + " value: '" + value + "'");
    }
}
