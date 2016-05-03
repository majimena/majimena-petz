package org.majimena.petical.datatype.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.majimena.petical.datatype.EnumDataType;

import java.io.IOException;

/**
 * Created by todoken on 2015/07/26.
 */
public abstract class EnumDataTypeDeserializer<T extends EnumDataType> extends JsonDeserializer<T> {

    @Override
    public T deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonToken token = parser.getCurrentToken();
        if (token == JsonToken.VALUE_STRING) {
            String value = parser.getText().trim();
            return newEnumDataType(value);
        }
        throw context.mappingException(handledType());
    }

    protected abstract T newEnumDataType(String name);

}
