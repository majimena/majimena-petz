package org.majimena.petical.config.audit;

import org.majimena.petical.datetime.L10nDateTimeProvider;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.auditing.DateTimeProvider;

import javax.inject.Named;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Audit用に現在日時を返すプロバイダ.
 */
@ComponentScan
public class AuditingDateTimeProvider implements DateTimeProvider {
    /**
     * {@inheritDoc}
     */
    @Override
    public Calendar getNow() {
        ZonedDateTime now = L10nDateTimeProvider.now();
        return GregorianCalendar.from(now);
    }
}
