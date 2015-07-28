package org.majimena.framework.core.datatypes.converters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * ISO 8601 date format
 * Jackson serializer for displaying LocalDate objects.
 */
public class ISO8601LocalDateSerializer extends JsonSerializer<LocalDate> {
    @Override
    public void serialize(LocalDate value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        String d = DateTimeFormatter.ISO_INSTANT.format(value);
        generator.writeString(d);
    }
}
