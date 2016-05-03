package org.majimena.petical.domain;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * 種別ドメインのシリアライザ.
 */
public class TypeSerializer extends JsonSerializer<Type> {
    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(Type value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        if (value != null) {
            generator.writeString(value.getName());
        }
    }
}
