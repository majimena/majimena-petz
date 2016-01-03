package org.majimena.petz.common.utils;

import java.time.LocalDateTime;

/**
 * 日時に関するユーティリティ.
 */
public class DateTimeUtils {

    /**
     * ミニマム時刻の日付に変換する.
     *
     * @param value 日時
     * @return ミニマム時刻の時刻
     */
    public static LocalDateTime minOfDay(LocalDateTime value) {
        return LocalDateTime.of(value.getYear(), value.getMonthValue(), value.getDayOfMonth(), 0, 0, 0);
    }

    /**
     * マックス時刻の日付に変換する.
     *
     * @param value 日時
     * @return マックス時刻の時刻
     */
    public static LocalDateTime maxOfDay(LocalDateTime value) {
        return LocalDateTime.of(value.getYear(), value.getMonthValue(), value.getDayOfMonth(), 23, 59, 59);
    }
}
