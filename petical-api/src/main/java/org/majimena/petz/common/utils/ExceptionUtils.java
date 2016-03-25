package org.majimena.petz.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petz.common.exceptions.ResourceNotFoundException;

/**
 * Created by todoken on 2015/11/15.
 */
public class ExceptionUtils {

    public static void throwIfNull(Object value) {
        if (value == null) {
            throw new ResourceNotFoundException("Object must not be null");
        }
    }

    public static void throwIfNotEqual(String value1, String value2) {
        if (!StringUtils.equals(value1, value2)) {
            throw new ResourceNotFoundException("value1 [" + value1 + "] must be equal value2 [" + value2 + "]");
        }
    }
}
