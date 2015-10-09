package org.majimena.petz.domain.common;

import org.apache.commons.lang3.StringUtils;
import org.majimena.framework.domain.converters.EnumDataTypeDeserializer;

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
