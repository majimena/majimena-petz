package org.majimena.petz.service.impl;

import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.majimena.petz.Application;
import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.ClinicStaff;
import org.majimena.petz.domain.User;
import org.majimena.petz.repository.ClinicRepository;
import org.majimena.petz.repository.ClinicStaffRepository;
import org.majimena.petz.repository.UserRepository;
import org.majimena.petz.security.SecurityUtils;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * @see org.majimena.petz.service.impl.ClinicServiceImpl
 */
@RunWith(Enclosed.class)
public class ClinicServiceImplTest {

    @RunWith(SpringJUnit4ClassRunner.class)
    @WebAppConfiguration
    @SpringApplicationConfiguration(classes = Application.class)
    public static class SaveClinicTest {

        private ClinicServiceImpl sut = new ClinicServiceImpl();

        @Mocked
        private ClinicRepository clinicRepository;

        @Mocked
        private ClinicStaffRepository clinicStaffRepository;

        @Mocked
        private UserRepository userRepository;

        @Mocked
        private SecurityUtils securityUtils;

        @Before
        public void before() {
            sut.setClinicRepository(clinicRepository);
            sut.setUserRepository(userRepository);
            sut.setClinicStaffRepository(clinicStaffRepository);
        }

        @Test
        public void 正しく登録されること() throws Exception {
            Clinic testData = Clinic.builder().code("test.clinic").name("テストクリニック").description("テストクリニックの説明").build();

            new NonStrictExpectations() {{
                clinicRepository.save(testData);
                result = Clinic.builder().id("1").code("test.clinic").name("テストクリニック").description("テストクリニックの説明").build();
                SecurityUtils.getCurrentUserId();
                result = 1L;
                userRepository.findOne("1");
                result = User.builder().id("1").build();
            }};

            Optional<Clinic> result = sut.saveClinic(testData);

            assertThat(result.get().getCode(), is("test.clinic"));
            assertThat(result.get().getName(), is("テストクリニック"));
            assertThat(result.get().getDescription(), is("テストクリニックの説明"));

            new Verifications() {{
                ClinicStaff staff;
                clinicStaffRepository.save(staff = withCapture());

                assertThat(staff.getId(), is(nullValue()));
                assertThat(staff.getClinic().getId(), is("1"));
                assertThat(staff.getUser().getId(), is("1"));
                assertThat(staff.getRole(), is("ROLE_OWNER"));
                assertThat(staff.getActivatedDate(), is(notNullValue()));
            }};
        }

        private User createTestUser() {
            User user = new User();
            user.setId("1");
            user.setLogin("test");
            return user;
        }
    }

}
