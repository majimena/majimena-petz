package org.majimena.framework.core.datatypes.converters;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.majimena.framework.core.datatypes.EnumDataType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by todoken on 2015/07/26.
 */
public abstract class EnumDataTypeDeserializer<T extends EnumDataType> extends JsonDeserializer<T> {

    @Override
    public T deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonToken token = parser.getCurrentToken();
        if (token == JsonToken.START_OBJECT) {
            Map<String, String> map = parser.readValueAs(HashMap.class);
            return newEnumDataType(map);
        }
        throw context.mappingException(handledType());
    }

    protected abstract T newEnumDataType(Map<String, String> map);

}
