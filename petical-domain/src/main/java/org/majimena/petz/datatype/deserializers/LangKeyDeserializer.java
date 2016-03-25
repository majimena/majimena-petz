package org.majimena.petz.datatype.deserializers;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petz.datatype.LangKey;

/**
 * 言語のJsonデシリアライザ.
 *
 * @see org.majimena.petz.datatype.LangKey
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
