package org.westminster.smartcampus.exception;

import org.westminster.smartcampus.model.ErrorMessage;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Global catch-all ExceptionMapper to prevent stack trace leaks.
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {
    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable throwable) {
        // Log the actual error internally for observability
        LOGGER.log(Level.SEVERE, "Unexpected server error", throwable);

        ErrorMessage error = new ErrorMessage(
                "An unexpected internal server error occurred. Please contact the administrator.",
                500,
                "https://smartcampus.westminster.ac.uk/api/docs/errors/500"
        );

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
