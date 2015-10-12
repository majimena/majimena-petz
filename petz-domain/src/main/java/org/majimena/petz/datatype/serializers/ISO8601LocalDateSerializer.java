package org.majimena.petz.datatype.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ISO 8601 date format
 * Jackson serializer for displaying LocalDate objects.
 */
public class ISO8601LocalDateSerializer extends JsonSerializer<LocalDate> {
    @Override
    public void serialize(LocalDate value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        if (value == null) {
            generator.writeNull();
        } else {
            // 日付のみの場合はどこのタイムゾーンか不明なのでユーザ設定から取得する
            // TODO とりあえず東京にしてあるが、ユーザー情報から取得しないとl10nできない
            ZonedDateTime zonedDateTime = value.atStartOfDay(ZoneId.of("JST", ZoneId.SHORT_IDS));
            String format = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            generator.writeString(format);
        }
    }
}
