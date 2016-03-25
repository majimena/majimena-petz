package org.majimena.petz.datatype.deserializers;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petz.datatype.TicketState;

/**
 * チケットステートのデシリアライザ.
 */
public class TicketStateDeserializer extends EnumDataTypeDeserializer<TicketState> {
    @Override
    protected TicketState newEnumDataType(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        return TicketState.valueOf(name);
    }
}
