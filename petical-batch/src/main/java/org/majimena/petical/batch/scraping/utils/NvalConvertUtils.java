package org.majimena.petical.batch.scraping.utils;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petical.common.utils.TrimUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.JapaneseChronology;
import java.time.chrono.JapaneseDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * nval.go.jp用のコンバータユーティリティ.
 */
public class NvalConvertUtils {

    private static Logger logger = LoggerFactory.getLogger(NvalConvertUtils.class);

    public static String getString(Map<String, String> map, String name) {
        String s = map.get(name);
        return TrimUtils.trim(s);
    }

    public static Boolean getBoolean(Map<String, String> map, String name) {
        String s = getString(map, name);
        if (StringUtils.equals("無し", s)) {
            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }
    }

    public static LocalDateTime getLocalDateTime(Map<String, String> map, String name) {
        String s = getString(map, name);
        try {
            if (StringUtils.isNotEmpty(s)) {
                DateTimeFormatter formatter = DateTimeFormatter
                        .ofPattern("GGGGy年M月d日")
                        .withChronology(JapaneseChronology.INSTANCE);
                ChronoLocalDate date = JapaneseDate.from(formatter.parse(s));
                LocalDate from = LocalDate.from(date);
                return from.atStartOfDay();
            }
        } catch (Exception e) {
            logger.warn("", e);
        }
        return null;
    }
}
