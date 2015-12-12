package org.majimena.petz.datetime;

import org.majimena.petz.datatype.TimeZone;
import org.majimena.petz.security.SecurityUtils;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * ローカル日時プロバイダ.
 */
public class L10nDateTimeProvider {

    /**
     * 現在日時を取得する（ローカライゼーションできるようにUTCベースの日時を返す）.
     *
     * @return UTCの現在日時
     */
    public static ZonedDateTime now() {
        return ZonedDateTime.now(TimeZone.UTC.getZoneId());
    }

    /**
     * 指定日をローカライズしてUTCに変換した日時オブジェクトを取得する.
     *
     * @param year  年
     * @param month 月
     * @param date  日
     * @return ローカル日時
     */
    public static LocalDateTime of(int year, int month, int date) {
        TimeZone timeZone = SecurityUtils.getCurrentTimeZone();
        ZonedDateTime dateTime = ZonedDateTime.of(year, month, date, 0, 0, 0, 0, timeZone.getZoneId());
        return LocalDateTime.ofInstant(dateTime.toInstant(), TimeZone.UTC.getZoneId());
    }

    /**
     * 指定月初日をローカライズしてUTCに変換した日時オブジェクトを取得する.
     *
     * @param year  年
     * @param month 月
     * @return ローカル日時
     */
    public static LocalDateTime of(int year, int month) {
        TimeZone timeZone = SecurityUtils.getCurrentTimeZone();
        ZonedDateTime dateTime = ZonedDateTime.of(year, month, 1, 0, 0, 0, 0, timeZone.getZoneId());
        return LocalDateTime.ofInstant(dateTime.toInstant(), TimeZone.UTC.getZoneId());
    }
}
