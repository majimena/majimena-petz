package org.majimena.petical.datatype.deserializers;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petical.datatype.CourseType;

/**
 * 診察科目のデシリアライザ.
 */
public class CourseTypeDeserializer extends EnumDataTypeDeserializer<CourseType> {
    @Override
    protected CourseType newEnumDataType(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        return CourseType.valueOf(name);
    }
}
