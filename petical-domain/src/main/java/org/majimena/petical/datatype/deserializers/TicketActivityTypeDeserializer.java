package org.majimena.petical.datatype.deserializers;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petical.datatype.TicketActivityType;

/**
 * チケットアクティビティのデシリアライザ.
 */
public class TicketActivityTypeDeserializer extends EnumDataTypeDeserializer<TicketActivityType> {
    @Override
    protected TicketActivityType newEnumDataType(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        return TicketActivityType.valueOf(name);
    }
}
