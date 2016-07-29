package org.majimena.petical.datatype.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.majimena.petical.datatype.TimeZone;
import org.majimena.petical.security.SecurityUtils;
import org.majimena.petical.security.authentication.PetzUserKey;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * ISO8601形式の日付にシリアライズするJSONシリアライザ.
 */
public class ISO8601ZonedDateTimeSerializer extends AbstractISO8601JsonSerializer<ZonedDateTime> {
    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(ZonedDateTime value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        // フォーマットした日時を書き出す
        generator.writeString(toISO8601String(value));
    }
}
