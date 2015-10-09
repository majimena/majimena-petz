package org.majimena.petz.domain.common;

import org.apache.commons.lang3.StringUtils;
import org.majimena.framework.domain.converters.EnumDataTypeDeserializer;

/**
 * Created by todoken on 2015/07/26.
 */
public class SexTypeDeserializer extends EnumDataTypeDeserializer<SexType> {
    @Override
    protected SexType newEnumDataType(String name) {
        if (StringUtils.isBlank(name)) {
            return SexType.NONE;
        }
        return SexType.valueOf(name);
    }
}
