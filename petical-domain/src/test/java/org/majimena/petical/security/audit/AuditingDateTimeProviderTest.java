package org.majimena.petical.security.audit;

import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.junit.Test;
import org.majimena.petical.datetime.L10nDateTimeProvider;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @see AuditingDateTimeProvider
 */
public class AuditingDateTimeProviderTest {

    private AuditingDateTimeProvider sut = new AuditingDateTimeProvider();

    @Mocked
    private L10nDateTimeProvider l10nDateTimeProvider;

    @Test
    public void プロバイダ経由でUTCの日付が取得できること() {
        ZonedDateTime dateTime = ZonedDateTime.of(2015, 10, 1, 0, 0, 0, 0, ZoneId.of("UTC"));
        new NonStrictExpectations() {{
            L10nDateTimeProvider.now();
            result = dateTime;
        }};

        assertThat(sut.getNow(), is(GregorianCalendar.from(dateTime)));
    }
}