package org.majimena.petz.datatype.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
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
        // サーバ側は全てUTCで日付を扱う
        ZonedDateTime utc = value.atZone(ZoneId.of("UTC"));

        // TODO とりあえず東京にしてあるが、ユーザー情報から取得しないとl10nできない
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(utc.toInstant(), ZoneId.of("JST", ZoneId.SHORT_IDS));
        String format = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        generator.writeString(format);
    }
}
