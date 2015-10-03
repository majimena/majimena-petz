package org.majimena.framework.domain.converters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * ISO 8601 date format
 * Jackson serializer for displaying LocalDate objects.
 */
public class ISO8601LocalDateSerializer extends JsonSerializer<LocalDate> {
    @Override
    public void serialize(LocalDate value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        LocalDateTime dateTime = value.atTime(LocalTime.MIN);
        // TODO とりあえず東京にしてあるが、ユーザー情報から取得しないとi18nできない
        ZonedDateTime atZone = dateTime.atZone(ZoneId.of("JST", ZoneId.SHORT_IDS));
        String format = atZone.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        generator.writeString(format);
    }
}
