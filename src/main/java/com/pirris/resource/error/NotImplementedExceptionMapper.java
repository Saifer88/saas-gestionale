package com.pirris.resource.error;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Maps {@link UnsupportedOperationException}, thrown by service methods that
 * are declared in the HTTP contract but not implemented yet, to a
 * {@code 501 Not Implemented} response with an {@link ApiError} body, instead
 * of a generic 500.
 */
@Provider
public class NotImplementedExceptionMapper implements ExceptionMapper<UnsupportedOperationException> {

    @Override
    public Response toResponse(UnsupportedOperationException exception) {
        ApiError body = ApiError.of(Response.Status.NOT_IMPLEMENTED.getStatusCode(),
                "Not Implemented", exception.getMessage(), null);
        return Response.status(Response.Status.NOT_IMPLEMENTED).entity(body).build();
    }
}
