package com.washingtonpost.dropwizard.exceptions.mappers;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.dropwizard.jersey.errors.ErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * <p>Tells Dropwizard how to send a Response when our code throws a JsonProcessingException</p>
 * <p>From https://goo.gl/uhbxtQ</p>
 */
@Provider
public class JsonProcessingExceptionMapper implements ExceptionMapper<JsonProcessingException> {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonProcessingExceptionMapper.class);
    private static final boolean DEFAULT_SHOW_DETAILS = true;
    private static final MediaType DEFAULT_MEDIA_TYPE = MediaType.APPLICATION_JSON_TYPE;
    private final boolean showDetails;
    private final MediaType mediaType;

    public JsonProcessingExceptionMapper() {
        this(DEFAULT_SHOW_DETAILS, DEFAULT_MEDIA_TYPE);
    }

    public JsonProcessingExceptionMapper(boolean showDetails, MediaType mediaType) {
        this.showDetails = showDetails;
        this.mediaType = mediaType;
    }

    @Override
    public Response toResponse(JsonProcessingException exception) {
        /*
         * If the error is in the JSON generation, it's a server error.
         */
        if (exception instanceof JsonGenerationException) {
            LOGGER.warn("Error generating JSON", exception);
            return Response
                    .serverError()
                    .type(this.mediaType)
                    .entity(new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                            "Error generating JSON",
                            showDetails? exception.getOriginalMessage() : null))
                    .build();
        }

        final String message = exception.getOriginalMessage();

        /*
         * If we can't deserialize the JSON because someone forgot a no-arg constructor, it's a
         * server error and we should inform the developer.
         */
        if (message.startsWith("No suitable constructor found")) {
            LOGGER.error("Unable to deserialize the specific type", exception);
            return Response
                    .serverError()
                    .type(this.mediaType)
                    .entity(new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                            "Unable to deserialize the specific type",
                            showDetails? exception.getOriginalMessage() : null))
                    .build();
        }

        /*
         * Otherwise, it's those pesky users.
         */
        LOGGER.debug("Unable to process JSON", exception);
        final ErrorMessage errorMessage = new ErrorMessage(Response.Status.BAD_REQUEST.getStatusCode(),
                "Unable to process JSON", showDetails ? message : null);
        return Response.status(Response.Status.BAD_REQUEST)
                .type(this.mediaType)
                .entity(errorMessage)
                .build();
    }
}
