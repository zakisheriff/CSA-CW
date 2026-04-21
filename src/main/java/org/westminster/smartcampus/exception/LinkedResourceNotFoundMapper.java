package org.westminster.smartcampus.exception;

import org.westminster.smartcampus.model.ErrorMessage;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Mapper for handled missing references in payload (422 Unprocessable Entity).
 */
@Provider
public class LinkedResourceNotFoundMapper implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Override
    public Response toResponse(LinkedResourceNotFoundException exception) {
        // HTTP 422 Unprocessable Entity is semantically accurate for valid JSON with invalid references
        ErrorMessage error = new ErrorMessage(
            exception.getMessage(),
            422,
            "https://smartcampus.westminster.ac.uk/api/docs/errors/422"
        );

        return Response.status(422)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
