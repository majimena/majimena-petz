package org.majimena.petz.service.impl;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.majimena.petz.Application;
import org.majimena.petz.datatype.TicketStatus;
import org.majimena.petz.datatype.TimeZone;
import org.majimena.petz.domain.Ticket;
import org.majimena.petz.domain.examination.TicketCriteria;
import org.majimena.petz.repository.AbstractSpringDBUnitTest;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.TicketService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @see TicketServiceImpl
 */
@RunWith(Enclosed.class)
public class ScheduleServiceImplIT {

    @WebAppConfiguration
    @SpringApplicationConfiguration(classes = Application.class)
    public static class GetSchedulesByTicketCriteriaTest extends AbstractSpringDBUnitTest {

        @Inject
        private TicketService sut;

        @Mocked
        private SecurityUtils securityUtils;

        @Test
        @DatabaseSetup("classpath:/fixture/base.xml")
        public void 指定月のスケジュールが取得できること() {
            new NonStrictExpectations() {{
                SecurityUtils.getCurrentTimeZone();
                result = TimeZone.ASIA_TOKYO;
            }};

            TicketCriteria criteria = new TicketCriteria("0", null, null, 2015, 11, null);
            List<Ticket> result = sut.getTicketsByTicketCriteria(criteria);

            assertThat(result.size(), is(2));
            assertThat(result.get(0).getId(), is("schedule1"));
            assertThat(result.get(0).getClinic().getId(), is("0"));
            assertThat(result.get(0).getPet().getId(), is("1"));
            assertThat(result.get(0).getCustomer().getId(), is("customer1"));
            assertThat(result.get(0).getMemo(), is("とりあえず１"));
            assertThat(result.get(0).getStatus(), is(TicketStatus.RESERVED));
            assertThat(result.get(0).getStartDateTime(), is(LocalDateTime.of(2015, 11, 1, 15, 0, 0)));
            assertThat(result.get(0).getEndDateTime(), is(LocalDateTime.of(2015, 11, 1, 15, 30, 0, 0)));
            assertThat(result.get(1).getId(), is("schedule4"));
            assertThat(result.get(1).getClinic().getId(), is("0"));
            assertThat(result.get(1).getPet().getId(), is("1"));
            assertThat(result.get(1).getCustomer().getId(), is("customer1"));
            assertThat(result.get(1).getMemo(), is("重複"));
            assertThat(result.get(1).getStatus(), is(TicketStatus.RESERVED));
            assertThat(result.get(1).getStartDateTime(), is(LocalDateTime.of(2015, 11, 30, 14, 59, 59)));
            assertThat(result.get(1).getEndDateTime(), is(LocalDateTime.of(2015, 12, 1, 15, 00, 00)));
        }

        @Test
        @DatabaseSetup("classpath:/fixture/base.xml")
        public void 指定日のスケジュールが取得できること() {
            new NonStrictExpectations() {{
                SecurityUtils.getCurrentTimeZone();
                result = TimeZone.ASIA_TOKYO;
            }};

            TicketCriteria criteria = new TicketCriteria("0", null, null, 2015, 10, 30);
            List<Ticket> result = sut.getTicketsByTicketCriteria(criteria);

            assertThat(result.size(), is(1));
            assertThat(result.get(0).getId(), is("schedule6"));
            assertThat(result.get(0).getClinic().getId(), is("0"));
            assertThat(result.get(0).getPet().getId(), is("1"));
            assertThat(result.get(0).getCustomer().getId(), is("customer1"));
            assertThat(result.get(0).getMemo(), is("ぎりぎり２"));
            assertThat(result.get(0).getStatus(), is(TicketStatus.RESERVED));
            assertThat(result.get(0).getStartDateTime(), is(LocalDateTime.of(2015, 10, 30, 14, 59, 59)));
            assertThat(result.get(0).getEndDateTime(), is(LocalDateTime.of(2015, 10, 31, 15, 0, 0, 0)));
        }
    }
}
