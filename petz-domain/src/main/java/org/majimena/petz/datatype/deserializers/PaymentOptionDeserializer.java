package org.majimena.petz.datatype.deserializers;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petz.datatype.PaymentOption;

/**
 * 支払方法のデシリアライザ.
 */
public class PaymentOptionDeserializer extends EnumDataTypeDeserializer<PaymentOption> {
    @Override
    protected PaymentOption newEnumDataType(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        return PaymentOption.valueOf(name);
    }
}
