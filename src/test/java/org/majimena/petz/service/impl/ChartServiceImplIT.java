package org.majimena.petz.service.impl;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.majimena.petz.Application;
import org.majimena.petz.common.exceptions.SystemException;
import org.majimena.petz.domain.*;
import org.majimena.petz.repository.AbstractSpringDBUnitTest;
import org.majimena.petz.service.ChartService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @see ChartServiceImpl
 */
@RunWith(Enclosed.class)
public class ChartServiceImplIT {

    @WebAppConfiguration
    @SpringApplicationConfiguration(classes = Application.class)
    public static class SaveChartTest extends AbstractSpringDBUnitTest {

        @Inject
        private ChartService sut;

        @Test
        @DatabaseSetup("classpath:/testdata/chart.xml")
        public void カルテが保存できること() {
            final Chart testData = Chart.builder()
                .clinic(Clinic.builder().id("0").build())
                .customer(Customer.builder().id("customer1").build())
                .pet(Pet.builder().name("test").type(new Type("toy")).color(new Color("white")).build())
                .build();

            Chart result = sut.saveChart("0", testData);

            assertThat(result.getId(), is(notNullValue()));
            assertThat(result.getClinic().getId(), is("0"));
            assertThat(result.getCustomer().getId(), is("customer1"));
            assertThat(result.getPet().getId(), is(notNullValue()));
            assertThat(result.getPet().getName(), is("test"));
            assertThat(result.getPet().getType(), is(new Type("toy")));
            assertThat(result.getPet().getColor(), is(new Color("white")));
            assertThat(result.getChartNo(), is(notNullValue()));
            assertThat(result.getCreationDate(), is(notNullValue()));
        }

        @Test(expected = SystemException.class)
        @DatabaseSetup("classpath:/testdata/chart.xml")
        public void 該当するクリニックがない場合はシステム例外になること() {
            final Chart testData = Chart.builder()
                .clinic(Clinic.builder().id("0").build())
                .customer(Customer.builder().id("customer1").build())
                .pet(Pet.builder().name("test").type(new Type("toy")).color(new Color("white")).build())
                .build();

            sut.saveChart("999", testData);
        }

        @Test(expected = SystemException.class)
        @DatabaseSetup("classpath:/testdata/chart.xml")
        public void 該当する顧客がない場合はシステム例外になること() {
            final Chart testData = Chart.builder()
                .clinic(Clinic.builder().id("0").build())
                .customer(Customer.builder().id("999").build())
                .pet(Pet.builder().name("test").type(new Type("toy")).color(new Color("white")).build())
                .build();

            sut.saveChart("0", testData);
        }
    }
}
