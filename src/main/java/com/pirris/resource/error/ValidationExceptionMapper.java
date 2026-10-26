package com.pirris.resource.error;

import java.util.List;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Maps bean-validation failures to a 400 response listing each violated field.
 */
@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        List<String> details = exception.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .sorted()
                .toList();
        ApiError body = ApiError.of(Response.Status.BAD_REQUEST.getStatusCode(),
                "Validation Failed", "One or more fields are invalid", details);
        return Response.status(Response.Status.BAD_REQUEST).entity(body).build();
    }
}
