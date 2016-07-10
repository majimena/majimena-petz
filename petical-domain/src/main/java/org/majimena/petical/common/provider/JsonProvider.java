package org.majimena.petical.common.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.majimena.petical.common.exceptions.SystemException;

/**
 * JSONプロバイダ.
 */
public class JsonProvider {
    /**
     * JSON文字列に変換する.
     *
     * @param o オブジェクト
     * @return JSON文字列
     */
    public static String toJson(Object o) {
        ObjectMapper mapper = ApplicationContextProvider.getBean(ObjectMapper.class);
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new SystemException(e);
        }
    }
}
