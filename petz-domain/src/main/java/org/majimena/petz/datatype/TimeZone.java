package org.majimena.petz.datatype;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * サポートするタイムゾーン.
 */
public enum TimeZone implements EnumDataType, Serializable {

    /**
     * 世界標準時.
     */
    UTC("UTC"),

    /**
     * 日本標準時.
     */
    ASIA_TOKYO("Asia/Tokyo");

    /**
     * ゾーンID.
     */
    private String value;

    /**
     * コンストラクタ.
     *
     * @param value ゾーンID
     */
    TimeZone(String value) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValue() {
        return name();
    }

    /**
     * ゾーンIDを取得する.
     *
     * @return ゾーンID
     */
    public ZoneId getZoneId() {
        return ZoneId.of(value);
    }

    /**
     * UTCの日時オブジェクトから指定のタイムゾーンを適用したゾーン日時を取得する.
     *
     * @param utc UTCゾーン日時
     * @return 指定のタイムゾーンを適用したゾーン日時
     */
    public ZonedDateTime fromInstant(ZonedDateTime utc) {
        return ZonedDateTime.ofInstant(utc.toInstant(), getZoneId());
    }

    /**
     * ローカル日時を指定のタイムゾーンを適用したゾーン日時に変換する.
     *
     * @param dateTime ローカル日時
     * @return 指定のタイムゾーンを適用したゾーン日時
     */
    public ZonedDateTime toZonedDateTime(LocalDateTime dateTime) {
        return dateTime.atZone(getZoneId());
    }
}
