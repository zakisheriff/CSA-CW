package org.westminster.smartcampus.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Mapper for standard JAX-RS WebApplicationExceptions.
 * Ensures that even framework-level errors (like 404 or 405) are returned as JSON ErrorResponse objects.
 */
@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

    @Override
    public Response toResponse(WebApplicationException exception) {
        Response response = exception.getResponse();
        int status = response.getStatus();
        
        ErrorResponse error = new ErrorResponse(
            status,
            response.getStatusInfo().getReasonPhrase(),
            exception.getMessage()
        );

        return Response.status(status)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
