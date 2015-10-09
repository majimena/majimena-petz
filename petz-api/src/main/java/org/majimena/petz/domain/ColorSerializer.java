package org.majimena.petz.domain;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * 毛色ドメインのシリアライザ.
 */
public class ColorSerializer extends JsonSerializer<Color> {
    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(Color value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        if (value != null) {
            generator.writeString(value.getName());
        }
    }
}
