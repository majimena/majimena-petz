package org.majimena.framework.core.datatypes.converters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Set;

/**
 * Created by todoken on 2015/07/26.
 */
public class StringSetSerializer extends JsonSerializer<Set<String>> {
    @Override
    public void serialize(Set<String> value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeStartArray();
        if (value != null) {
            value.stream().forEach(s -> {
                try {
                    generator.writeString(s);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        generator.writeEndArray();
    }
}
