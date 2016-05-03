package org.majimena.petical.datatype.deserializers;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petical.datatype.InvoiceState;

/**
 * インヴォイスステートのデシリアライザ.
 */
public class InvoiceStateDeserializer extends EnumDataTypeDeserializer<InvoiceState> {
    @Override
    protected InvoiceState newEnumDataType(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        return InvoiceState.valueOf(name);
    }
}
