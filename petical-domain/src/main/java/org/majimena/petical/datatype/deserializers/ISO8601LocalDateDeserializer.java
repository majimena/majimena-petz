package org.majimena.petical.datatype.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * ISO 8601 date format
 * Jackson deserializer for displaying LocalDate objects.
 */
public class ISO8601LocalDateDeserializer extends JsonDeserializer<LocalDate> {

    @Override
    public LocalDate deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonToken token = parser.getCurrentToken();
        if (token == JsonToken.VALUE_STRING) {
            String value = parser.getText().trim();
            return LocalDate.parse(value, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }
        throw context.mappingException(handledType());
    }
}
