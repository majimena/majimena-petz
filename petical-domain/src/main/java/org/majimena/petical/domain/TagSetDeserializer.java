package org.majimena.petical.domain;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * タグの一覧ドメインのデシリアライザ.
 */
public class TagSetDeserializer extends JsonDeserializer<Set<Tag>> {
    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Tag> deserialize(JsonParser jp, DeserializationContext context) throws IOException {
        JsonToken token = jp.getCurrentToken();
        if (token == JsonToken.START_ARRAY) {
            Set<Tag> set = new HashSet<>();
            while (jp.nextToken() != JsonToken.END_ARRAY) {
                String str = jp.getValueAsString();
                if (StringUtils.isNotEmpty(str)) {
                    set.add(new Tag(str));
                }
            }
            return set;
        }
        throw context.mappingException(handledType());
    }
}
