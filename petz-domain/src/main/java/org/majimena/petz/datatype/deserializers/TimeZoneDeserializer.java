package org.majimena.petz.datatype.deserializers;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petz.datatype.TimeZone;

/**
 * タイムゾーンのJsonデシリアライザ.
 *
 * @see org.majimena.petz.datatype.TimeZone
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
