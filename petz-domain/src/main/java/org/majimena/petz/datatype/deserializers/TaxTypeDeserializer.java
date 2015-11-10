package org.majimena.petz.datatype.deserializers;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petz.datatype.TaxType;

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
