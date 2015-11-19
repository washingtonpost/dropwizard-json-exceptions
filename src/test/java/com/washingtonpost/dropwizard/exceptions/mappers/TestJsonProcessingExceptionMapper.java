package com.washingtonpost.dropwizard.exceptions.mappers;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ExceptionMapper;
import org.json.JSONException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * <p>Tests the exception mapping of our JsonProcessor</p>
 */
public class TestJsonProcessingExceptionMapper extends AbstractExceptionMapperTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonProcessingExceptionMapper.class);
    private static final ExceptionMapper DEFAULT_JSON_MAPPER = new JsonProcessingExceptionMapper();

    @Test
    public void testJsonGenerationException() throws JsonProcessingException, JSONException {
        runJsonAssertionTest(DEFAULT_JSON_MAPPER,
                new JsonGenerationException("There was a problem"),
                500,
                "jsonGenerationException-expected.json");
    }

    @Test
    public void testJsonProcessingNoSuitableConstructor() throws JSONException {
        runJsonAssertionTest(DEFAULT_JSON_MAPPER,
                new MyJPE("No suitable constructor found for blah"),
                500,
                "jsonProcessingNoSuitableConstructor-expected.json");
    }

    @Test
    public void testJsonProcessingException() throws JSONException {
        runJsonAssertionTest(DEFAULT_JSON_MAPPER,
                new MyJPE("Something went wrong here!"),
                400,
                "jsonProcessingException-expected.json");
    }

    /**
     * If we don't construct our ExceptionMapper with a "true" flag for whether or not to show details,
     * we should get a slightly less verbose output
     * @throws org.json.JSONException
     */
    @Test
    public void testJsonProcessingExceptionWithNoDetailsMapper() throws JSONException {
        ExceptionMapper mapper = new JsonProcessingExceptionMapper(false, MediaType.APPLICATION_JSON_TYPE);
        runJsonAssertionTest(mapper,
                new MyJPE("Something went wrong here!"),
                400,
                "jsonProcessingExceptionNoDetails-expected.json");
    }

    @Test
    public void testJsonProcessingExceptionWithXMLOutputType() throws JSONException, IOException, SAXException {
        ExceptionMapper mapper = new JsonProcessingExceptionMapper(true, MediaType.APPLICATION_XML_TYPE);
        runXmlAssertionTest(mapper,
                            new MyJPE("Something went wrong here!"),
                            400,
                            "testJsonProcessingExceptionWithXMLOutputType-expected.xml");
    }

    // Cuz the actual JsonProcessingException is all protected
    private class MyJPE extends JsonProcessingException {
        public MyJPE(String message) {
            super(message);
        }
    }
}
