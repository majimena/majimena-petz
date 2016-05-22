package io.petical.batch.scraping.utils;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * {@link NvalConvertUtils}
 */
public class NvalConvertUtilsTest {
    @Test
    public void 和暦をLocalDateTimeに変換できること() {
        Map<String, String> map = new HashMap<>();
        map.put("date1", "平成28年1月31日");
        LocalDateTime date0 = NvalConvertUtils.getLocalDateTime(map, "date0");
        LocalDateTime date1 = NvalConvertUtils.getLocalDateTime(map, "date1");

        assertNull(date0);
        assertNotNull(date1);
        assertEquals("2016-01-31T00:00", date1.toString());
    }
}