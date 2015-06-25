package org.majimena.framework.persistence.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * LocalDateTimeの永続化用コンバータ.
 */
@Converter(autoApply = true)
public class LocalDateTimePersistenceConverter implements AttributeConverter<LocalDateTime, Timestamp> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp convertToDatabaseColumn(LocalDateTime entityValue) {
        return Timestamp.valueOf(entityValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime convertToEntityAttribute(Timestamp databaseValue) {
        return databaseValue.toLocalDateTime();
    }

}
