package org.majimena.petz.repository;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.majimena.petz.Application;
import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.ClinicStaff;
import org.majimena.petz.domain.User;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @see ClinicStaffRepository
 */
@RunWith(Enclosed.class)
public class ClinicStaffRepositoryIT {

    @WebAppConfiguration
    @SpringApplicationConfiguration(classes = Application.class)
    public static class SampleTest extends AbstractSpringDBUnitTest {

        @Inject
        private ClinicStaffRepository sut;

        @Inject
        private EntityManager entityManager;

        @Test
        @DatabaseSetup("classpath:/testdata/clinic_staff.xml")
        public void sampleTest() throws Exception {
            Clinic clinic = entityManager.find(Clinic.class, 1L);
            User user = entityManager.find(User.class, 1L);

            sut.save(new ClinicStaff(null, clinic, user, "ROLE_DOCTOR", LocalDate.now()));

            List<ClinicStaff> results = sut.findAll();
            assertThat(results.size(), is(2));
            ClinicStaff result = results.get(results.size() - 1);
            assertThat(result.getId(), is(notNullValue()));
            assertThat(result.getClinic().getId(), is(1L));
            assertThat(result.getUser().getId(), is(1L));
            assertThat(result.getRole(), is("ROLE_DOCTOR"));
        }
    }

}
