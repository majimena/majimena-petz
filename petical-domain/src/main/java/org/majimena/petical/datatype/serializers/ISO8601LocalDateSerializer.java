package org.majimena.petical.datatype.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.majimena.petical.security.authentication.PetzUserKey;
import org.majimena.petical.datatype.TimeZone;
import org.majimena.petical.security.SecurityUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * ISO8601形式の日付にシリアライズするJSONシリアライザ.
 */
public class ISO8601LocalDateSerializer extends AbstractISO8601JsonSerializer<LocalDate> {
    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(LocalDate value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        // ユーザのタイムゾーン設定を適用
        ZonedDateTime dateTime = SecurityUtils.getPrincipal()
                .map(user -> Optional.ofNullable(user.get(PetzUserKey.TIMEZONE, TimeZone.class))
                        .map(timeZone -> value.atStartOfDay(timeZone.getZoneId()))
                        .orElse(value.atStartOfDay(TimeZone.UTC.getZoneId())))
                .orElse(value.atStartOfDay(TimeZone.UTC.getZoneId()));

        // フォーマットした日時を書き出す
        generator.writeString(toISO8601String(dateTime));
    }
}
