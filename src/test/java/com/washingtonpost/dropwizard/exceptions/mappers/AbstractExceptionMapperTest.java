package com.washingtonpost.dropwizard.exceptions.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.xml.XmlMapper;
import io.dropwizard.jersey.errors.ErrorMessage;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.json.JSONException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * <p>Abstract test holding a couple common helper functions</p>
 */
public abstract class AbstractExceptionMapperTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractExceptionMapperTest.class);
    protected static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * @param resourcePath The path to the resource to load
     * @return The full URL of the {@code resourcePath}
     */
    protected URL getResource(String resourcePath) {
        return getClass().getClassLoader().getResource(resourcePath);
    }

    /**
     * @param fixtureName The name of the fixture in the same path as the test being executed, e.g. "credit".
     * @return The full URL of a {@code fixtureName} file sitting in the same relative directory as the caller
     */
    protected URL getSisterPathResource(String fixtureName) {
        return getResource(getClass().getPackage().getName().replace(".", "/") + "/" + fixtureName);
    }

    /**
     * @param fixtureName The name of a fixture file to load into a String
     * @return The contents of the fixtureFile, in one big 'ol string
     */
    protected String loadSisterPathFixture(String fixtureName) {
        URL sisterPathResource = getSisterPathResource(fixtureName);
        String fixtureContents;
        try (Scanner scanner = new Scanner(sisterPathResource.openStream(), "UTF-8")) {
            // From "stupid scanner tricks" https://weblogs.java.net/blog/pat/archive/2004/10/stupid_scanner.html
            fixtureContents = scanner.useDelimiter("\\A").next();
            scanner.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return fixtureContents;
    }

    protected void runJsonAssertionTest(ExceptionMapper mapper,
                                        Exception exception,
                                        int expectedStatusCode,
                                        String expectedFixture) throws JSONException {
        Response response = mapper.toResponse(exception);
        assertEquals(expectedStatusCode, response.getStatus());
        assertTrue(response.hasEntity());

        String actualJson = readJsonPayload(response.getEntity());
        String expectedJson = loadSisterPathFixture(expectedFixture);

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT);
    }

    protected String readJsonPayload(Object entity) {
        ErrorMessage errorMessage = (ErrorMessage)entity;
        try {
            String json = objectMapper.writeValueAsString(errorMessage);
            return json;
        } catch (JsonProcessingException ex) {
            fail("Unable to parse JSON out of entity " + errorMessage);
        }
        return null;
    }

    protected void runXmlAssertionTest(ExceptionMapper mapper,
                                       Exception exception,
                                       int expectedStatusCode,
                                       String expectedFixture) throws JSONException, IOException, SAXException {
        Response response = mapper.toResponse(exception);
        assertEquals(expectedStatusCode, response.getStatus());
        assertTrue(response.hasEntity());

        String actualXML = readXmlPayload(response.getEntity());
        LOGGER.debug("actualXML = {}", actualXML);


        String expectedXML = loadSisterPathFixture(expectedFixture);

        XMLUnit.setIgnoreWhitespace(true);
        XMLAssert.assertXMLEqual(expectedXML, actualXML);
    }

    protected String readXmlPayload(Object entity) throws IOException {
        ErrorMessage errorMessage = (ErrorMessage)entity;
        XmlMapper xmlMapper = new XmlMapper();
        return xmlMapper.writeValueAsString(errorMessage);
    }

}
