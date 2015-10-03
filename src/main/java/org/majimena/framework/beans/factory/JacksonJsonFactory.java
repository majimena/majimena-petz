package org.majimena.framework.beans.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by k.todoroki on 2015/08/08.
 */
public class JacksonJsonFactory implements JsonFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(JacksonJsonFactory.class);

    private ObjectMapper objectMapper;

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public <T> String to(final T object) {
        try {
            if (object == null) {
                return StringUtils.EMPTY;
            }
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            LOGGER.warn("cannot convert object to json.", e);
            return StringUtils.EMPTY;
        }
    }

    @Override
    public <T> T from(final String json, final Class<T> clazz) {
        try {
            if (StringUtils.isBlank(json)) {
                return null;
            }
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            LOGGER.warn("cannot convert json to object.", e);
            return null;
        }
    }
}
