package org.majimena.petical.repository;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.majimena.petical.Application;
import org.majimena.petical.domain.Clinic;
import org.majimena.petical.domain.ClinicStaff;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

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
            List<Clinic> results = sut.findClinicsByUserId("1");

            assertThat(results.size(), is(2));
            assertThat(results.get(0).getId(), is("1"));
            assertThat(results.get(0).getName(), is("Name1"));
            assertThat(results.get(0).getDescription(), is("Description1"));
            assertThat(results.get(1).getId(), is("2"));
            assertThat(results.get(1).getName(), is("Name2"));
            assertThat(results.get(1).getDescription(), is("Description2"));
        }
    }

    @WebAppConfiguration
    @SpringApplicationConfiguration(classes = Application.class)
    public static class FindByClinicIdTest extends AbstractSpringDBUnitTest {

        @Inject
        private ClinicStaffRepository sut;

        @Test
        @DatabaseSetup("classpath:/testdata/clinic_staff.xml")
        public void データベースの内容が正しく取得できること() throws Exception {
            List<ClinicStaff> result = sut.findByClinicId("1");

            assertThat(result.size(), is(2));
            assertThat(result.get(0).getId(), is("1"));
            assertThat(result.get(0).getClinic().getId(), is("1"));
            assertThat(result.get(0).getUser().getId(), is("1"));
            assertThat(result.get(0).getRole(), is("ROLE_ADMIN"));
            assertThat(result.get(1).getId(), is("2"));
            assertThat(result.get(1).getClinic().getId(), is("1"));
            assertThat(result.get(1).getUser().getId(), is("2"));
            assertThat(result.get(1).getRole(), is("ROLE_ADMIN"));
        }

        @Test
        @DatabaseSetup("classpath:/testdata/clinic_staff.xml")
        public void 該当データがない場合は空のオプショナルが取得できること() throws Exception {
            List<ClinicStaff> result = sut.findByClinicId("999");

            assertThat(result.isEmpty(), is(true));
        }
    }

    @WebAppConfiguration
    @SpringApplicationConfiguration(classes = Application.class)
    public static class FindByClinicIdAndUserIdTest extends AbstractSpringDBUnitTest {

        @Inject
        private ClinicStaffRepository sut;

        @Test
        @DatabaseSetup("classpath:/testdata/clinic_staff.xml")
        public void データベースの内容が正しく取得できること() throws Exception {
            Optional<ClinicStaff> result = sut.findByClinicIdAndUserId("1", "1");

            assertThat(result.isPresent(), is(true));
            assertThat(result.get().getId(), is("1"));
            assertThat(result.get().getClinic().getId(), is("1"));
            assertThat(result.get().getUser().getId(), is("1"));
            assertThat(result.get().getRole(), is("ROLE_ADMIN"));
        }

        @Test
        @DatabaseSetup("classpath:/testdata/clinic_staff.xml")
        public void 該当データがない場合は空のオプショナルが取得できること() throws Exception {
            Optional<ClinicStaff> result = sut.findByClinicIdAndUserId("999", "999");

            assertThat(result.isPresent(), is(false));
        }
    }

    @WebAppConfiguration
    @SpringApplicationConfiguration(classes = Application.class)
    public static class DeleteByClinicIdTest extends AbstractSpringDBUnitTest {

        @Inject
        private ClinicStaffRepository sut;

        @Test
        @Transactional
        @DatabaseSetup("classpath:/testdata/clinic_staff.xml")
        public void データベースの内容が正しく削除されること() throws Exception {
            assertThat(sut.count(), is(3L));

            sut.deleteByClinicId("1");
            sut.flush();

            assertThat(sut.count(), is(1L));
            List<ClinicStaff> result = sut.findByClinicId("1");
            assertThat(result.size(), is(0));
        }

        @Test
        @Transactional
        @DatabaseSetup("classpath:/testdata/clinic_staff.xml")
        public void 該当データがない場合はデータが削除されないこと() throws Exception {
            assertThat(sut.count(), is(3L));

            sut.deleteByClinicId("999");
            sut.flush();

            assertThat(sut.count(), is(3L));
        }
    }
}
