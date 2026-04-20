package org.westminster.smartcampus.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Mapper for business logic exceptions.
 */
@Provider
public class SpecificExceptionMapper implements ExceptionMapper<RuntimeException> {

    @Override
    public Response toResponse(RuntimeException exception) {
        ErrorResponse error;
        Response.Status status;

        if (exception instanceof RoomNotEmptyException) {
            status = Response.Status.CONFLICT; // 409
            error = new ErrorResponse(409, "Conflict", exception.getMessage());
        } else if (exception instanceof LinkedResourceNotFoundException) {
            status = Response.Status.BAD_REQUEST; // Using 400 as a safe fallback, or 422 if supported
            // Actually, spec says 422 or 400. 422 is better.
            error = new ErrorResponse(422, "Unprocessable Entity", exception.getMessage());
            return Response.status(422).entity(error).type(MediaType.APPLICATION_JSON).build();
        } else if (exception instanceof SensorUnavailableException) {
            status = Response.Status.FORBIDDEN; // 403
            error = new ErrorResponse(403, "Forbidden", exception.getMessage());
        } else {
            // Let other exceptions fall through to the global mapper or default handler
            return null; 
        }

        return Response.status(status)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
