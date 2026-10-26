package com.pirris.resource.error;

/**
 * Thrown by resources/services when an entity cannot be found by id.
 * Mapped to HTTP 404 by {@link NotFoundExceptionMapper}.
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }

    public static NotFoundException of(String entity, Object id) {
        return new NotFoundException(entity + " not found: " + id);
    }
}
