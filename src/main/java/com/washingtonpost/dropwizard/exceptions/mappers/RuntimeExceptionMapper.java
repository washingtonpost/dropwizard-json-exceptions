package com.washingtonpost.dropwizard.exceptions.mappers;

import io.dropwizard.jersey.errors.ErrorMessage;
import java.io.PrintWriter;
import java.io.StringWriter;
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
 * <p>The RuntimeExceptionMapper has a DEFAULT_SHOW_DETAILS set to false because it's questionable security to emit
 * stack traces from a REST API and it makes upgrading & testing the exception mapper hard because comparing against stack trace
 * Strings for equality or near-equality is pretty lame</p>
 */
@Provider
public class RuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuntimeExceptionMapper.class);
    private static final boolean DEFAULT_SHOW_DETAILS = false;
    private static final MediaType DEFAUT_MEDIA_TYPE = MediaType.APPLICATION_JSON_TYPE;
    private final boolean showDetails;
    private final MediaType mediaType;

    public RuntimeExceptionMapper() {
        this(DEFAULT_SHOW_DETAILS, DEFAUT_MEDIA_TYPE);
    }

    public RuntimeExceptionMapper(boolean showDetails, MediaType mediaType) {
        this.showDetails = showDetails;
        this.mediaType = mediaType;
    }

    @Override
    public Response toResponse(RuntimeException exception) {

        // Build default response
        Response defaultResponse = Response.serverError()
                .type(this.mediaType)
                .entity(new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
                                         exception.getMessage(),
                                         this.showDetails ? stackTraceToString(exception) : null))
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
                    .type(this.mediaType)
                    .entity(new ErrorMessage(Response.Status.UNAUTHORIZED.getStatusCode(), 
                                             exception.getMessage(),
                                             this.showDetails ? stackTraceToString(exception) : null))
                    .build();
        }
        if (webAppException.getResponse().getStatus() == 404) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .type(this.mediaType)
                    .entity(new ErrorMessage(Response.Status.NOT_FOUND.getStatusCode(), 
                                             exception.getMessage(),
                                             this.showDetails ? stackTraceToString(exception) : null))
                    .build();
        }

        LOGGER.error(exception.getMessage(), exception);

        return defaultResponse;
    }

    // Basically the same as org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(Throwable), without the dependency
    private String stackTraceToString(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        return stringWriter.toString();
    }
}
