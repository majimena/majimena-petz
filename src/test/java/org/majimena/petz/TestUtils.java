package org.majimena.petz;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.datatype.joda.ser.DateTimeSerializer;
import com.fasterxml.jackson.datatype.joda.ser.JacksonJodaFormat;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.datetime.joda.DateTimeFormatterFactory;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Utility class for testing REST controllers.
 */
public class TestUtils {

    /** MediaType for JSON UTF8 */
    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(
            MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    /**
     * Convert an object to JSON byte array.
     *
     * @param object
     *            the object to convert
     * @return the JSON byte array
     * @throws IOException
     */
    public static byte[] convertObjectToJsonBytes(Object object)
            throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        JodaModule module = new JodaModule();
        DateTimeFormatterFactory formatterFactory = new DateTimeFormatterFactory();
        formatterFactory.setIso(DateTimeFormat.ISO.DATE);
        module.addSerializer(DateTime.class, new DateTimeSerializer(
            new JacksonJodaFormat(formatterFactory.createDateTimeFormatter()
                .withZoneUTC())));
        mapper.registerModule(module);
        return mapper.writeValueAsBytes(object);
    }

    public static class Type {
        public static final String TYPE_400 = "http://httpstatus.es/400";
    }

    public static class Title {
        public static final String VALIDATION_FAILED = "Validation Failed";
    }

    public static class Detail {
        public static final String VALIDATION_FAILED = "The content you've send contains validation errors.";
    }

    public static class Message {
        public static final String NULL = "may not be null";
        public static final String EMAIL = "invalid email address";
    }

}
