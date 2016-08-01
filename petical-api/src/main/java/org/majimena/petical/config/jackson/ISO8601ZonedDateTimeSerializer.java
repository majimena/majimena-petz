package org.majimena.petical.config.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.ZonedDateTime;

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
