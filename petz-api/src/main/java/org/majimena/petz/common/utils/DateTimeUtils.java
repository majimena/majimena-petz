package org.majimena.petz.common.utils;

import org.majimena.petz.datetime.L10nDateTimeProvider;

import java.time.LocalDateTime;
import java.util.Optional;

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

    public static LocalDateTime from(Integer year, Integer month, Integer day) {
        return Optional.ofNullable(day)
                .map(p -> L10nDateTimeProvider.of(year, month, p))
                .orElse(L10nDateTimeProvider.of(year, month));
    }

    public static LocalDateTime to(Integer year, Integer month, Integer day) {
        LocalDateTime from = from(year, month, day);
        return Optional.ofNullable(day)
                .map(p -> from.plusDays(1))
                .orElse(from.plusMonths(1));
    }
}
