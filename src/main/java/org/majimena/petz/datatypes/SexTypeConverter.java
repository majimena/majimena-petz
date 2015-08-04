package org.majimena.petz.datatypes;

import javax.persistence.AttributeConverter;

/**
 * Created by todoken on 2015/07/26.
 */
public class SexTypeConverter implements AttributeConverter<SexType, String> {
    @Override
    public String convertToDatabaseColumn(SexType attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getValue();
    }

    @Override
    public SexType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return SexType.valueOf(dbData);
    }
}
