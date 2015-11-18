package com.washingtonpost.dropwizard.exceptions.mappers;

import io.dropwizard.jersey.errors.ErrorMessage;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * From http://gary-rowe.com/agilestack/2012/10/23/how-to-implement-a-runtimeexceptionmapper-for-dropwizard/</p>
 * <p>
 * Provider to provide the following to Jersey framework:</p>
 * <ul>
 * <li>Provision of general runtime exception to response mapping</li>
 * </ul>
 */
@Provider
public class RuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuntimeExceptionMapper.class);
    private static final MediaType MEDIA_TYPE = MediaType.APPLICATION_JSON_TYPE;

    @Override
    public Response toResponse(RuntimeException exception) {

        // Build default response
        Response defaultResponse = Response.serverError()
                .type(MEDIA_TYPE)
                .entity(new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), exception.getMessage()))
                .build();

        // Check for any specific handling
        if (exception instanceof WebApplicationException) {
            return handleWebApplicationException(exception, defaultResponse);
        }

        // Use the default
        LOGGER.error(exception.getMessage(), exception);
        return defaultResponse;
    }

    private Response handleWebApplicationException(RuntimeException exception, Response defaultResponse) {
        WebApplicationException webAppException = (WebApplicationException) exception;

        // No logging
        if (webAppException.getResponse().getStatus() == 401) {
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .type(MEDIA_TYPE)
                    .entity(new ErrorMessage(Response.Status.UNAUTHORIZED.getStatusCode(), exception.getMessage()))
                    .build();
        }
        if (webAppException.getResponse().getStatus() == 404) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .type(MEDIA_TYPE)
                    .entity(new ErrorMessage(Response.Status.NOT_FOUND.getStatusCode(), exception.getMessage()))
                    .build();
        }

        LOGGER.error(exception.getMessage(), exception);

        return defaultResponse;
    }

}
