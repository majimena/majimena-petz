package org.majimena.petical.config.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Booleanのシリアライザ.
 */
public class BooleanSerializer extends JsonSerializer<Boolean> {
    @Override
    public void serialize(Boolean value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        if (value == Boolean.TRUE) {
            generator.writeString("1");
        } else {
            generator.writeString("0");
        }
    }
}
