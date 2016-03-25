package org.majimena.petz;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import org.majimena.petz.datatype.deserializers.ISO8601LocalDateDeserializer;
import org.majimena.petz.datatype.deserializers.ISO8601LocalDateTimeDeserializer;
import org.majimena.petz.datatype.serializers.ISO8601LocalDateSerializer;
import org.majimena.petz.datatype.serializers.ISO8601LocalDateTimeSerializer;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Utility class for testing REST controllers.
 */
public class TestUtils {

    /**
     * MediaType for JSON UTF8
     */
    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(
            MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    /**
     * Convert an object to JSON byte array.
     *
     * @param object the object to convert
     * @return the JSON byte array
     * @throws IOException
     */
    public static byte[] convertObjectToJsonBytes(Object object)
            throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        JSR310Module module = new JSR310Module();
        module.addSerializer(LocalDate.class, new ISO8601LocalDateSerializer());
        module.addSerializer(LocalDateTime.class, new ISO8601LocalDateTimeSerializer());
        module.addDeserializer(LocalDate.class, new ISO8601LocalDateDeserializer());
        module.addDeserializer(LocalDateTime.class, new ISO8601LocalDateTimeDeserializer());
        mapper.registerModule(module);
        return mapper.writeValueAsBytes(object);
    }

    public static class Type {
        public static final String TYPE_400 = "http://httpstatus.es/400";
        public static final String TYPE_401 = "http://httpstatus.es/401";
    }

    public static class Title {
        public static final String VALIDATION_FAILED = "Validation Failed";
        public static final String CONVERSION_FAILED = "Conversion Failed";
        public static final String UNAUTHORIZED = "Unauthorized";
    }

    public static class Detail {
        public static final String VALIDATION_FAILED = "The content you've send contains validation errors.";
    }

    public static class Message {
        public static final String CANNOT_ACCESS = "cannot access resource";
        public static final String NULL = "may not be null";
        public static final String EMPTY = "may not be empty";
        public static final String EMAIL = "not a well-formed email address";
    }
}
