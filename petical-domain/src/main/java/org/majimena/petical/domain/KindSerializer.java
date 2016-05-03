package org.majimena.petical.domain;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * 種類ドメインのシリアライザ.
 */
public class KindSerializer extends JsonSerializer<Kind> {
    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(Kind value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        if (value != null) {
            generator.writeString(value.getName());
        }
    }
}
