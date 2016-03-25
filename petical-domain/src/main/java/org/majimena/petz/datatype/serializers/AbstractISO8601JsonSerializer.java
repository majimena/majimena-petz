package org.majimena.petz.datatype.serializers;

import com.fasterxml.jackson.databind.JsonSerializer;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ISO8601関連のJSONシリアライザの抽象クラス.
 */
public abstract class AbstractISO8601JsonSerializer<T> extends JsonSerializer<T> {
    /**
     * ゾーン日時オブジェクトをISO8601形式の文字列に変換する.
     *
     * @param dateTime ゾーン日時
     * @return ISO8601形式の文字列
     */
    protected static String toISO8601String(ZonedDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}
