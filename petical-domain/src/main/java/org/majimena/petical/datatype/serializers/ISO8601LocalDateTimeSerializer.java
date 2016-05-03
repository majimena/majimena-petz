package org.majimena.petical.datatype.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.majimena.petical.datatype.TimeZone;
import org.majimena.petical.domain.authentication.PetzUserKey;
import org.majimena.petical.security.SecurityUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * ISO8601形式の日時にシリアライズするJSONシリアライザ.
 */
public class ISO8601LocalDateTimeSerializer extends AbstractISO8601JsonSerializer<LocalDateTime> {
    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(LocalDateTime value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        // サーバ側は全てUTCで日付を扱う
        ZonedDateTime utc = TimeZone.UTC.toZonedDateTime(value);

        // ユーザのタイムゾーン設定を適用
        ZonedDateTime dateTime = SecurityUtils.getPrincipal()
                .map(user -> Optional.ofNullable(user.get(PetzUserKey.TIMEZONE, TimeZone.class))
                        .map(timeZone -> timeZone.fromInstant(utc))
                        .orElse(utc))
                .orElse(utc);

        // フォーマットした日時を書き出す
        generator.writeString(toISO8601String(dateTime));
    }
}
