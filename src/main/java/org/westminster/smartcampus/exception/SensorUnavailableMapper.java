package org.westminster.smartcampus.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Mapper for handling forbidden states (403 Forbidden).
 */
@Provider
public class SensorUnavailableMapper implements ExceptionMapper<SensorUnavailableException> {

    @Override
    public Response toResponse(SensorUnavailableException exception) {
        ErrorResponse error = new ErrorResponse(
            Response.Status.FORBIDDEN.getStatusCode(),
            "Forbidden",
            exception.getMessage()
        );

        return Response.status(Response.Status.FORBIDDEN)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
