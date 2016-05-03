package org.majimena.petical.datatype.deserializers;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petical.datatype.LangKey;

/**
 * 言語のJsonデシリアライザ.
 *
 * @see org.majimena.petical.datatype.LangKey
 */
public class LangKeyDeserializer extends EnumDataTypeDeserializer<LangKey> {
    @Override
    protected LangKey newEnumDataType(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        return LangKey.valueOf(name);
    }
}
