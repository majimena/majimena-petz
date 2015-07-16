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
import org.majimena.petz.common.exceptions.SystemException;
import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.ClinicInvitation;
import org.majimena.petz.domain.ClinicStaff;
import org.majimena.petz.domain.User;
import org.majimena.petz.domain.user.Roles;
import org.majimena.petz.repository.ClinicInvitationRepository;
import org.majimena.petz.repository.ClinicRepository;
import org.majimena.petz.repository.ClinicStaffRepository;
import org.majimena.petz.repository.UserRepository;
import org.majimena.petz.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @see ClinicInvitationServiceImpl
 */
@RunWith(Enclosed.class)
public class ClinicInvitationServiceImplTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClinicInvitationServiceImplTest.class);

    @RunWith(SpringJUnit4ClassRunner.class)
    @WebAppConfiguration
    @SpringApplicationConfiguration(classes = Application.class)
    public static class InviteStaffTest {

        private ClinicInvitationServiceImpl sut = new ClinicInvitationServiceImpl();

        @Inject
        private SpringTemplateEngine templateEngine;

        @Mocked
        private ClinicRepository clinicRepository;

        @Mocked
        private ClinicInvitationRepository clinicInvitationRepository;

        @Mocked
        private UserRepository userRepository;

        @Mocked
        private EmailManager emailManager;

        @Mocked
        private SecurityUtils securityUtils;

        @Before
        public void before() {
            sut.setClinicRepository(clinicRepository);
            sut.setClinicInvitationRepository(clinicInvitationRepository);
            sut.setUserRepository(userRepository);
            sut.setEmailManager(emailManager);
            sut.setTemplateEngine(templateEngine);
            sut.setFromEmail("noreply@majimena.org");
        }

        @Test
        public void クリニックの招待状が送信されること_既にユーザが存在する場合() throws Exception {
            new NonStrictExpectations() {{
                SecurityUtils.getCurrentLogin();
                result = "login";
                userRepository.findOneByLogin("login");
                result = Optional.of(User.builder().id("1").login("login").langKey("ja").build());
                clinicRepository.findOne("10");
                result = Clinic.builder().id("10").code("clinic.com").name("テストクリニック").description("テストクリニックの説明").build();
                userRepository.findOneByEmail("test@mail.com");
                result = Optional.of(User.builder().id("100").email("test@mail.com").build());
            }};

            sut.inviteStaff("10", new HashSet<>(Arrays.asList("test@mail.com")));

            new Verifications() {{
                ClinicInvitation invitation;
                clinicInvitationRepository.save(invitation = withCapture());
                assertThat(invitation.getClinic(), is(notNullValue()));
                assertThat(invitation.getUser(), is(notNullValue()));
                assertThat(invitation.getEmail(), is("test@mail.com"));
                assertThat(invitation.getActivationKey(), is(notNullValue()));

                String to;
                String from;
                String subject;
                String content;
                emailManager.send(to = withCapture(), from = withCapture(), subject = withCapture(), content = withCapture());
                assertThat(to, is("test@mail.com"));
                assertThat(from, is("noreply@majimena.org"));
                assertThat(subject, is(notNullValue()));
                assertThat(content, is(notNullValue()));

                LOGGER.debug(content);
            }};
        }

        @Test(expected = SystemException.class)
        public void クリニックの招待状が送信されることログインユーザが存在しない場合() throws Exception {
            new NonStrictExpectations() {{
                SecurityUtils.getCurrentLogin();
                result = "login";
                userRepository.findOneByLogin("login");
                result = Optional.empty();
            }};

            sut.inviteStaff("10", new HashSet<>(Arrays.asList("test@mail.com")));
        }
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @WebAppConfiguration
    @SpringApplicationConfiguration(classes = Application.class)
    public static class ActivateTest {

        private ClinicInvitationServiceImpl sut = new ClinicInvitationServiceImpl();

        @Mocked
        private ClinicRepository clinicRepository;

        @Mocked
        private ClinicInvitationRepository clinicInvitationRepository;

        @Mocked
        private ClinicStaffRepository clinicStaffRepository;

        @Mocked
        private UserRepository userRepository;

        @Mocked
        private SecurityUtils securityUtils;

        @Before
        public void before() {
            sut.setClinicRepository(clinicRepository);
            sut.setClinicInvitationRepository(clinicInvitationRepository);
            sut.setClinicStaffRepository(clinicStaffRepository);
            sut.setUserRepository(userRepository);
            sut.setFromEmail("noreply@majimena.org");
        }

        @Test
        public void 招待がアクティベートできること() throws Exception {
            new NonStrictExpectations() {{
                SecurityUtils.getCurrentLogin();
                result = "foo";
                clinicInvitationRepository.findOne("1");
                result = ClinicInvitation.builder().id("1").email("").activationKey("1234567890")
                    .user(User.builder().id("10").login("login").build())
                    .clinic(Clinic.builder().id("100").name("テストクリニック").build()).build();
                userRepository.findOneByLogin("foo");
                result = Optional.of(User.builder().id("1000").login("foo").langKey("ja").build());
            }};

            sut.activate("1", "1234567890");

            new Verifications() {{
                ClinicStaff staff;
                clinicStaffRepository.save(staff = withCapture());
                assertThat(staff.getClinic().getId(), is("100"));
                assertThat(staff.getClinic().getName(), is("テストクリニック"));
                assertThat(staff.getUser().getId(), is("1000"));
                assertThat(staff.getUser().getLogin(), is("foo"));
                assertThat(staff.getRole(), is(Roles.ROLE_STAFF.name()));
                assertThat(staff.getActivatedDate(), is(notNullValue()));

                ClinicInvitation invitation;
                clinicInvitationRepository.delete(invitation = withCapture());
                assertThat(invitation.getId(), is("1"));
            }};
        }

        @Test(expected = SystemException.class)
        public void ログインユーザが存在しない場合はシステムエラーになること() throws Exception {
            new NonStrictExpectations() {{
                SecurityUtils.getCurrentLogin();
                result = "foo";
                clinicInvitationRepository.findOne("1");
                result = ClinicInvitation.builder().id("1").email("").activationKey("1234567890")
                    .user(User.builder().id("10").login("login").build())
                    .clinic(Clinic.builder().id("100").name("テストクリニック").build()).build();
                userRepository.findOneByLogin("foo");
                result = Optional.empty();
            }};

            sut.activate("1", "1234567890");
        }
    }

}
