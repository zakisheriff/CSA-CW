package org.westminster.smartcampus.exception;

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
        ErrorResponse error = new ErrorResponse(
            422,
            "Unprocessable Entity",
            exception.getMessage()
        );

        return Response.status(422)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
