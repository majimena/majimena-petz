package org.majimena.petz.datatype.deserializers;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petz.datatype.ScheduleStatus;
import org.majimena.petz.datatype.deserializers.EnumDataTypeDeserializer;

/**
 * Created by todoken on 2015/10/04.
 */
public class ScheduleStatusDeserializer extends EnumDataTypeDeserializer<ScheduleStatus> {
    @Override
    protected ScheduleStatus newEnumDataType(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        return ScheduleStatus.valueOf(name);
    }
}
