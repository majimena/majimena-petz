package org.majimena.framework.domain.converters;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by todoken on 2015/08/05.
 */
public class StringSetDeserializer extends JsonDeserializer<Set<String>> {

    @Override
    public Set<String> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonToken token = parser.getCurrentToken();
        if (token == JsonToken.START_ARRAY) {
            Set<String> set = new HashSet<>();
            while (parser.nextToken() != JsonToken.END_ARRAY) {
                set.add(parser.getValueAsString());
            }
            return set;
        }
        throw context.mappingException(handledType());
    }

}
