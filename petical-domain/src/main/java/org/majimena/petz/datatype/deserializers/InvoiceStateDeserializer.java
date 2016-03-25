package org.majimena.petz.datatype.deserializers;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petz.datatype.InvoiceState;

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
