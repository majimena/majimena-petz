package org.majimena.petz.datatype.deserializers;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petz.datatype.TicketStatus;

/**
 * Created by todoken on 2015/10/04.
 */
public class ScheduleStatusDeserializer extends EnumDataTypeDeserializer<TicketStatus> {
    @Override
    protected TicketStatus newEnumDataType(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        return TicketStatus.valueOf(name);
    }
}
