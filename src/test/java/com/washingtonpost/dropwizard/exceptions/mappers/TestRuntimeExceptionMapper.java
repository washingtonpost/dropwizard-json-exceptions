package com.washingtonpost.dropwizard.exceptions.mappers;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import org.json.JSONException;
import org.junit.Test;

/**
 * <p>Tests the exception mapping of our RuntimeExceptionMapper</p>
 */
public class TestRuntimeExceptionMapper extends AbstractExceptionMapperTest {

    private static final ExceptionMapper DEFAULT_MAPPER = new RuntimeExceptionMapper();

    @Test
    public void testIllegalStateException() throws JSONException {
        runJsonAssertionTest(DEFAULT_MAPPER,
                             new IllegalStateException("Oops! an ISE!"),
                             500,
                             "illegalStateException-expected.json");

    }

    @Test
    public void testWebAppExceptionUnauthorized() throws JSONException {
        runJsonAssertionTest(DEFAULT_MAPPER,
                             new WebApplicationException("You're not allowed in here!", Response.Status.UNAUTHORIZED),
                             401,
                             "webAppException401-expected.json");
    }

    @Test
    public void testWebAppExceptionNotFound() throws JSONException {
        runJsonAssertionTest(DEFAULT_MAPPER,
                             new WebApplicationException("Can't find it!", Response.Status.NOT_FOUND),
                             404,
                             "webAppException404-expected.json");
    }

    @Test
    public void testWebAppExceptionContentType() throws JSONException {
        runJsonAssertionTest(DEFAULT_MAPPER,
                             new WebApplicationException("Wrong type", Response.Status.UNSUPPORTED_MEDIA_TYPE),
                             415,
                             "webAppException415-expected.json");
    }
}
