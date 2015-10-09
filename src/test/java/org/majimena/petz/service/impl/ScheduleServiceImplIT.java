package org.majimena.petz.service.impl;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.majimena.petz.Application;
import org.majimena.petz.domain.Schedule;
import org.majimena.petz.domain.common.ScheduleStatus;
import org.majimena.petz.domain.examination.ScheduleCriteria;
import org.majimena.petz.repository.AbstractSpringDBUnitTest;
import org.majimena.petz.service.ScheduleService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @see ScheduleServiceImpl
 */
@RunWith(Enclosed.class)
public class ScheduleServiceImplIT {

    @WebAppConfiguration
    @SpringApplicationConfiguration(classes = Application.class)
    public static class GetSchedulesByScheduleCriteriaTest extends AbstractSpringDBUnitTest {

        @Inject
        private ScheduleService sut;

        @Test
        @DatabaseSetup("classpath:/fixture/base.xml")
        public void 指定月のスケジュールが取得できること() {
            ScheduleCriteria criteria = new ScheduleCriteria("0", 2015, 11, null);

            List<Schedule> result = sut.getSchedulesByScheduleCriteria(criteria);

            assertThat(result.size(), is(2));
            assertThat(result.get(0).getId(), is("schedule1"));
            assertThat(result.get(0).getClinic().getId(), is("0"));
            assertThat(result.get(0).getUser().getId(), is("1"));
            assertThat(result.get(0).getPet().getId(), is("1"));
            assertThat(result.get(0).getCustomer().getId(), is("customer1"));
            assertThat(result.get(0).getMemo(), is("とりあえず１"));
            assertThat(result.get(0).getStatus(), is(ScheduleStatus.RESERVED));
            assertThat(result.get(0).getStartDateTime(), is(LocalDateTime.of(2015, 11, 1, 0, 0, 0)));
            assertThat(result.get(0).getEndDateTime(), is(LocalDateTime.of(2015, 11, 1, 0, 30, 0, 0)));
            assertThat(result.get(1).getId(), is("schedule4"));
            assertThat(result.get(1).getClinic().getId(), is("0"));
            assertThat(result.get(1).getUser().getId(), is("1"));
            assertThat(result.get(1).getPet().getId(), is("1"));
            assertThat(result.get(1).getCustomer().getId(), is("customer1"));
            assertThat(result.get(1).getMemo(), is("重複"));
            assertThat(result.get(1).getStatus(), is(ScheduleStatus.RESERVED));
            assertThat(result.get(1).getStartDateTime(), is(LocalDateTime.of(2015, 11, 30, 23, 59, 0)));
            assertThat(result.get(1).getEndDateTime(), is(LocalDateTime.of(2015, 12, 1, 23, 59, 0, 0)));
        }

        @Test
        @DatabaseSetup("classpath:/fixture/base.xml")
        public void 指定日のスケジュールが取得できること() {
            ScheduleCriteria criteria = new ScheduleCriteria("0", 2015, 10, 30);

            List<Schedule> result = sut.getSchedulesByScheduleCriteria(criteria);

            assertThat(result.size(), is(1));
            assertThat(result.get(0).getId(), is("schedule6"));
            assertThat(result.get(0).getClinic().getId(), is("0"));
            assertThat(result.get(0).getUser().getId(), is("1"));
            assertThat(result.get(0).getPet().getId(), is("1"));
            assertThat(result.get(0).getCustomer().getId(), is("customer1"));
            assertThat(result.get(0).getMemo(), is("ぎりぎり２"));
            assertThat(result.get(0).getStatus(), is(ScheduleStatus.RESERVED));
            assertThat(result.get(0).getStartDateTime(), is(LocalDateTime.of(2015, 10, 30, 23, 59, 0)));
            assertThat(result.get(0).getEndDateTime(), is(LocalDateTime.of(2015, 11, 1, 0, 0, 0, 0)));
        }
    }
}
