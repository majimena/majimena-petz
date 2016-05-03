package org.majimena.petical.datatype.deserializers;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petical.datatype.PaymentType;

/**
 * 支払方法のデシリアライザ.
 */
public class PaymentTypeDeserializer extends EnumDataTypeDeserializer<PaymentType> {
    @Override
    protected PaymentType newEnumDataType(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        return PaymentType.valueOf(name);
    }
}
