package org.majimena.petz.common.utils;

import org.junit.Test;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @see DateTimeUtils
 */
public class DateTimeUtilsTest {

    @Test
    public void testMinOfDay() {
        LocalDateTime now = LocalDateTime.of(2016, 1, 1, 23, 59);
        LocalDateTime result = DateTimeUtils.minOfDay(now);

        assertThat(result.getYear(), is(2016));
        assertThat(result.getMonthValue(), is(1));
        assertThat(result.getDayOfMonth(), is(1));
        assertThat(result.getHour(), is(0));
        assertThat(result.getMinute(), is(0));
        assertThat(result.getSecond(), is(0));
    }

    @Test
    public void testMaxOfDay() {
        LocalDateTime now = LocalDateTime.of(2016, 1, 1, 0, 0);
        LocalDateTime result = DateTimeUtils.maxOfDay(now);

        assertThat(result.getYear(), is(2016));
        assertThat(result.getMonthValue(), is(1));
        assertThat(result.getDayOfMonth(), is(1));
        assertThat(result.getHour(), is(23));
        assertThat(result.getMinute(), is(59));
        assertThat(result.getSecond(), is(59));
    }
}
