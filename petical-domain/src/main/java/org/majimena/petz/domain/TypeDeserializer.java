package org.majimena.petz.domain;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * 種別ドメインのデシリアライザ.
 */
public class TypeDeserializer extends JsonDeserializer<Type> {
    /**
     * {@inheritDoc}
     */
    @Override
    public Type deserialize(JsonParser jp, DeserializationContext context) throws IOException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_STRING) {
            String str = jp.getText();
            if (StringUtils.isNotEmpty(str)) {
                return new Type(str);
            }
            return null;
        }
        throw context.mappingException(handledType());
    }
}
