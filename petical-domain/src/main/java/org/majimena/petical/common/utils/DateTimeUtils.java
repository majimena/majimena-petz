package org.majimena.petical.common.utils;

import org.majimena.petical.datetime.L10nDateTimeProvider;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

/**
 * 日時に関するユーティリティ.
 */
public class DateTimeUtils {

    /**
     * ミニマム時刻のローカル日時に変換する.
     *
     * @param value ローカル日時
     * @return ミニマム時刻のローカル日時
     */
    public static LocalDateTime minOfDay(LocalDateTime value) {
        return LocalDateTime.of(value.getYear(), value.getMonthValue(), value.getDayOfMonth(), 0, 0, 0);
    }

    /**
     * マックス時刻のローカル日時に変換する.
     *
     * @param value ローカル日時
     * @return マックス時刻のローカル日時
     */
    public static LocalDateTime maxOfDay(LocalDateTime value) {
        return LocalDateTime.of(value.getYear(), value.getMonthValue(), value.getDayOfMonth(), 23, 59, 59);
    }

    /**
     * ミニマム時刻のローカル日時に変換する.
     *
     * @param date ローカル日付
     * @return ミニマム時刻のローカル日時
     */
    public static LocalDateTime minOfDay(LocalDate date) {
        return LocalDateTime.of(date, LocalTime.MIN);
    }

    /**
     * マックス時刻のローカル日時に変換する.
     *
     * @param date ローカル日付
     * @return マックス時刻のローカル日時
     */
    public static LocalDateTime maxOfDay(LocalDate date) {
        return LocalDateTime.of(date, LocalTime.MAX);
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
