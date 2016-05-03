package org.majimena.petical.datatype.deserializers;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petical.datatype.TaxType;

/**
 * 消費税タイプのデシリアライザ.
 */
public class TaxTypeDeserializer extends EnumDataTypeDeserializer<TaxType> {
    @Override
    protected TaxType newEnumDataType(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        return TaxType.valueOf(name);
    }
}
