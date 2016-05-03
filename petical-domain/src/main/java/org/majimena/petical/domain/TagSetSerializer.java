package org.majimena.petical.domain;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Set;

/**
 * タグの一覧ドメインのシリアライザ.
 */
public class TagSetSerializer extends JsonSerializer<Set<Tag>> {
    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(Set<Tag> value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeStartArray();
        if (value != null) {
            value.stream().forEach(t -> {
                try {
                    if (t != null) {
                        generator.writeString(t.getName());
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        generator.writeEndArray();
    }
}
