package org.majimena.petz.service.impl;

import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.majimena.petz.Application;
import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.User;
import org.majimena.petz.repository.ClinicRepository;
import org.majimena.petz.repository.UserRepository;
import org.majimena.petz.security.SecurityUtils;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @see org.majimena.petz.service.impl.ClinicServiceImpl
 */
@RunWith(Enclosed.class)
public class ClinicServiceImplTest {

    @RunWith(SpringJUnit4ClassRunner.class)
    @WebAppConfiguration
    @SpringApplicationConfiguration(classes = Application.class)
    public static class SaveProjectTest {

        private ClinicServiceImpl sut = new ClinicServiceImpl();

        @Mocked
        private ClinicRepository clinicRepository;

        @Mocked
        private UserRepository userRepository;

        @Mocked
        private SecurityUtils securityUtils;

        @Before
        public void before() {
            sut.setClinicRepository(clinicRepository);
            sut.setUserRepository(userRepository);
        }

        @Test
        public void 正しく登録されること() throws Exception {
            Clinic testData = Clinic.builder().code("test.clinic").name("テストクリニック").description("テストクリニックの説明").build();

            new NonStrictExpectations() {{
                securityUtils.getCurrentLogin();
                result = "test";
                userRepository.findOneByLogin("test");
                result = Optional.of(createTestUser());
                clinicRepository.save(testData);
                result = testData;
            }};

            Optional<Clinic> result = sut.saveClinic(testData);

            assertThat(result.isPresent()).isTrue();
            assertThat(result.get().getCode()).isEqualTo("test.clinic");
            assertThat(result.get().getName()).isEqualTo("テストクリニック");
            assertThat(result.get().getDescription()).isEqualTo("テストクリニックの説明");
            assertThat(result.get().getOwnerUser().getId()).isEqualTo(1L);
            assertThat(result.get().getOwnerUser().getLogin()).isEqualTo("test");
        }

        private User createTestUser() {
            User user = new User();
            user.setId(1L);
            user.setLogin("test");
            return user;
        }
    }

}
