package org.majimena.petz.domain;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * 血液型ドメインのシリアライザ.
 */
public class BloodSerializer extends JsonSerializer<Blood> {
    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(Blood value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        if (value != null) {
            generator.writeString(value.getName());
        }
    }
}
