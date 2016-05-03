package org.majimena.petical.datatype.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Date;
import java.time.LocalDate;

/**
 * LocalDateの永続化用コンバータ.
 */
@Converter(autoApply = true)
public class LocalDatePersistenceConverter implements AttributeConverter<LocalDate, Date> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Date convertToDatabaseColumn(LocalDate entityValue) {
        if (entityValue == null) {
            return null;
        }
        return Date.valueOf(entityValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDate convertToEntityAttribute(Date databaseValue) {
        if (databaseValue == null) {
            return null;
        }
        return databaseValue.toLocalDate();
    }

}
