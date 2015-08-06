package org.majimena.framework.core.datatypes.converters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ISO 8601 date format
 * Jackson serializer for displaying LocalDate objects.
 */
public class ISO8601LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
    @Override
    public void serialize(LocalDateTime value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        // TODO とりあえず東京にしてあるが、ユーザー情報から取得しないとi18nできない
        ZonedDateTime atZone = value.atZone(ZoneId.of("JST", ZoneId.SHORT_IDS));
        String format = atZone.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        generator.writeString(format);
    }
}
