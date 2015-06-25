package org.majimena.petz.service.impl;

import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.majimena.framework.core.managers.EmailManager;
import org.majimena.petz.Application;
import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.ClinicStaff;
import org.majimena.petz.domain.User;
import org.majimena.petz.domain.user.Roles;
import org.majimena.petz.repository.ClinicRepository;
import org.majimena.petz.repository.ClinicStaffRepository;
import org.majimena.petz.repository.UserRepository;
import org.majimena.petz.security.SecurityUtils;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashSet;
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

            assertThat(result.isPresent(), is(true));
            assertThat(result.get().getCode(), is("test.clinic"));
            assertThat(result.get().getName(), is("テストクリニック"));
            assertThat(result.get().getDescription(), is("テストクリニックの説明"));
            assertThat(result.get().getOwnerUser().getId(), is(1L));
            assertThat(result.get().getOwnerUser().getLogin(), is("test"));
        }

        private User createTestUser() {
            User user = new User();
            user.setId(1L);
            user.setLogin("test");
            return user;
        }
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @WebAppConfiguration
    @SpringApplicationConfiguration(classes = Application.class)
    public static class InviteStaffTest {

        private ClinicServiceImpl sut = new ClinicServiceImpl();

        @Inject
        private SpringTemplateEngine templateEngine;

        @Mocked
        private ClinicRepository clinicRepository;

        @Mocked
        private ClinicStaffRepository clinicStaffRepository;

        @Mocked
        private UserRepository userRepository;

        @Mocked
        private EmailManager emailManager;

        @Mocked
        private SecurityUtils securityUtils;

        @Before
        public void before() {
            sut.setClinicRepository(clinicRepository);
            sut.setClinicStaffRepository(clinicStaffRepository);
            sut.setUserRepository(userRepository);
            sut.setEmailManager(emailManager);
            sut.setTemplateEngine(templateEngine);
            sut.setFromEmail("noreply@majimena.org");
        }

        @Test
        public void クリニックの招待状が送信されること_既にユーザが存在する場合() throws Exception {
            new NonStrictExpectations() {{
                securityUtils.getCurrentLogin();
                result = "login";
                userRepository.findOneByLogin("login");
                result = Optional.of(User.builder().id(1L).login("login").langKey("ja").build());
                clinicRepository.findOne(10L);
                result = Clinic.builder().id(10L).code("clinic.com").name("テストクリニック").description("テストクリニックの説明").build();
                userRepository.findOneByEmail("test@mail.com");
                result = Optional.of(User.builder().id(100L).email("test@mail.com").build());
            }};

            sut.inviteStaff(10L, new HashSet<>(Arrays.asList("test@mail.com")));

            new Verifications() {{
                ClinicStaff staff;
                clinicStaffRepository.save(staff = withCapture());
                assertThat(staff.getClinic(), is(notNullValue()));
                assertThat(staff.getUser(), is(notNullValue()));
                assertThat(staff.getEmail(), is("test@mail.com"));
                assertThat(staff.getActivated(), is(false));
                assertThat(staff.getRole(), is(Roles.ROLE_STAFF.name()));
                assertThat(staff.getActivationKey(), is(notNullValue()));

                String to;
                String from;
                String subject;
                String content;
                emailManager.send(to = withCapture(), from = withCapture(), subject = withCapture(), content = withCapture());
                assertThat(to, is("test@mail.com"));
                assertThat(from, is("noreply@majimena.org"));
                assertThat(subject, is(notNullValue()));
                assertThat(content, is(notNullValue()));
            }};
        }

        @Test
        public void クリニックの招待状が送信されること_まだユーザが存在しない場合() throws Exception {
            new NonStrictExpectations() {{
                securityUtils.getCurrentLogin();
                result = "login";
                userRepository.findOneByLogin("login");
                result = Optional.of(User.builder().id(1L).login("login").langKey("ja").build());
                clinicRepository.findOne(10L);
                result = Clinic.builder().id(10L).code("clinic.com").name("テストクリニック").description("テストクリニックの説明").build();
                userRepository.findOneByEmail("test@mail.com");
                result = Optional.empty();
            }};

            sut.inviteStaff(10L, new HashSet<>(Arrays.asList("test@mail.com")));

            new Verifications() {{
                ClinicStaff staff;
                clinicStaffRepository.save(staff = withCapture());
                assertThat(staff.getClinic(), is(notNullValue()));
                assertThat(staff.getUser(), is(nullValue()));
                assertThat(staff.getEmail(), is("test@mail.com"));
                assertThat(staff.getActivated(), is(false));
                assertThat(staff.getRole(), is(Roles.ROLE_STAFF.name()));
                assertThat(staff.getActivationKey(), is(notNullValue()));

                String to;
                String from;
                String subject;
                String content;
                emailManager.send(to = withCapture(), from = withCapture(), subject = withCapture(), content = withCapture());
                assertThat(to, is("test@mail.com"));
                assertThat(from, is("noreply@majimena.org"));
                assertThat(subject, is(notNullValue()));
                assertThat(content, is(notNullValue()));
            }};
        }
    }

}
