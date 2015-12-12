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
 * @see L10nDateTimeProvider
 */
public class L10nDateTimeProviderTest {

    @Mocked
    private SecurityUtils securityUtils;

    @Test
    public void testNow() throws Exception {
        new NonStrictExpectations() {{
            SecurityUtils.getCurrentTimeZone();
            result = TimeZone.ASIA_TOKYO;
        }};
        assertThat(L10nDateTimeProvider.now(), is(notNullValue()));
        System.out.println(L10nDateTimeProvider.now());
    }

    @Test
    public void ローカライズ後にUTCに変換された日付が取得できる事() throws Exception {
        new NonStrictExpectations() {{
            SecurityUtils.getCurrentTimeZone();
            result = TimeZone.ASIA_TOKYO;
        }};
        assertThat(L10nDateTimeProvider.of(2015, 1, 1).toString(), is("2014-12-31T15:00"));
    }

    @Test
    public void ローカライズ後にUTCに変換された月初日が取得できること() {
        new NonStrictExpectations() {{
            SecurityUtils.getCurrentTimeZone();
            result = TimeZone.ASIA_TOKYO;
        }};
        assertThat(L10nDateTimeProvider.of(2015, 1).toString(), is("2014-12-31T15:00"));
    }
}