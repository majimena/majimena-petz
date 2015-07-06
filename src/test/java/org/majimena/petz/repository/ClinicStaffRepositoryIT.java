package org.majimena.petz.repository;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.majimena.petz.Application;
import org.majimena.petz.domain.Clinic;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @see ClinicStaffRepository
 */
@RunWith(Enclosed.class)
public class ClinicStaffRepositoryIT {

    @WebAppConfiguration
    @SpringApplicationConfiguration(classes = Application.class)
    public static class FindClinicsByUserIdTest extends AbstractSpringDBUnitTest {

        @Inject
        private ClinicStaffRepository sut;

        @Test
        @DatabaseSetup("classpath:/testdata/clinic_staff.xml")
        public void データベースの内容が正しく取得できること() throws Exception {
            Page<Clinic> results = sut.findClinicsByUserId("1", new PageRequest(0, 10));

            assertThat(results.getTotalPages(), is(1));
            assertThat(results.getSize(), is(10));
            assertThat(results.getTotalElements(), is(2L));
            assertThat(results.getContent().size(), is(2));
            assertThat(results.getContent().get(0).getId(), is("1"));
            assertThat(results.getContent().get(0).getName(), is("Name1"));
            assertThat(results.getContent().get(0).getDescription(), is("Description1"));
            assertThat(results.getContent().get(1).getId(), is("2"));
            assertThat(results.getContent().get(1).getName(), is("Name2"));
            assertThat(results.getContent().get(1).getDescription(), is("Description2"));
        }
    }
}
