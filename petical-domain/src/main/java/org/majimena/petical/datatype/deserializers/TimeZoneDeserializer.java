package org.majimena.petical.datatype.deserializers;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petical.datatype.TimeZone;

/**
 * タイムゾーンのJsonデシリアライザ.
 *
 * @see org.majimena.petical.datatype.TimeZone
 */
public class TimeZoneDeserializer extends EnumDataTypeDeserializer<TimeZone> {
    @Override
    protected TimeZone newEnumDataType(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        return TimeZone.valueOf(name);
    }
}
