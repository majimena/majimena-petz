package org.majimena.petz.datetime;

import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.junit.Test;
import org.majimena.petz.datatype.TimeZone;
import org.majimena.petz.security.SecurityUtils;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @see LocalDateTimeProvider
 */
public class LocalDateTimeProviderTest {

    @Mocked
    private SecurityUtils securityUtils;

    @Test
    public void testNow() throws Exception {
        new NonStrictExpectations() {{
            SecurityUtils.getCurrentTimeZone();
            result = TimeZone.ASIA_TOKYO;
        }};
        assertThat(LocalDateTimeProvider.now(), is(notNullValue()));
        System.out.println(LocalDateTimeProvider.now());
    }

    @Test
    public void ローカライズ後にUTCに変換された日付が取得できる事() throws Exception {
        new NonStrictExpectations() {{
            SecurityUtils.getCurrentTimeZone();
            result = TimeZone.ASIA_TOKYO;
        }};
        assertThat(LocalDateTimeProvider.of(2015, 1, 1).toString(), is("2014-12-31T15:00"));
    }

    @Test
    public void ローカライズ後にUTCに変換された月初日が取得できること() {
        new NonStrictExpectations() {{
            SecurityUtils.getCurrentTimeZone();
            result = TimeZone.ASIA_TOKYO;
        }};
        assertThat(LocalDateTimeProvider.of(2015, 1).toString(), is("2014-12-31T15:00"));
    }
}