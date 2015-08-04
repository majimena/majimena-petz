package org.majimena.petz.datatypes;

import org.majimena.framework.core.datatypes.converters.EnumDataTypeDeserializer;

import java.util.Map;

/**
 * Created by todoken on 2015/07/26.
 */
public class SexTypeDeserializer extends EnumDataTypeDeserializer<SexType> {
    @Override
    protected SexType newEnumDataType(String name) {
        return SexType.valueOf(name);
    }
}
