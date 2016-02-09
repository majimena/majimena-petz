package org.majimena.petz.service.impl;

import mockit.Injectable;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.majimena.petz.common.exceptions.SystemException;
import org.majimena.petz.datatype.LangKey;
import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.ClinicInvitation;
import org.majimena.petz.domain.ClinicStaff;
import org.majimena.petz.domain.User;
import org.majimena.petz.domain.user.Roles;
import org.majimena.petz.manager.MailManager;
import org.majimena.petz.repository.ClinicInvitationRepository;
import org.majimena.petz.repository.ClinicRepository;
import org.majimena.petz.repository.ClinicStaffRepository;
import org.majimena.petz.repository.UserRepository;
import org.majimena.petz.security.SecurityUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * @see ClinicInvitationServiceImpl
 */
@RunWith(Enclosed.class)
public class ClinicInvitationServiceImplTest {

    public static class InviteStaffTest {

        @Tested
        private ClinicInvitationServiceImpl sut = new ClinicInvitationServiceImpl();
        @Injectable
        private ClinicRepository clinicRepository;
        @Injectable
        private ClinicInvitationRepository clinicInvitationRepository;
        @Injectable
        private ClinicStaffRepository clinicStaffRepository;
        @Injectable
        private UserRepository userRepository;
        @Injectable
        private MailManager mailManager;

        @Test
        public void クリニックの招待状が送信されること_既にユーザが存在する場合(@Mocked SecurityUtils utils) throws Exception {
            new NonStrictExpectations() {{
                clinicRepository.findOne("10");
                result = Clinic.builder().id("10").name("テストクリニック").description("テストクリニックの説明").build();
                userRepository.findOne("1");
                result = User.builder().id("1").login("login").langKey(LangKey.JAPANESE).build();
                userRepository.findOneByLogin("test@mail.com");
                result = Optional.of(User.builder().id("2").login("login").langKey(LangKey.JAPANESE).build());
            }};

            sut.inviteStaff("10", "1", new HashSet<>(Arrays.asList("test@mail.com")));

            new Verifications() {{
                ClinicInvitation invitation;
                clinicInvitationRepository.save(invitation = withCapture());
                assertThat(invitation.getClinic(), is(notNullValue()));
                assertThat(invitation.getUser(), is(notNullValue()));
                assertThat(invitation.getEmail(), is("test@mail.com"));
                assertThat(invitation.getActivationKey(), is(notNullValue()));

                String to;
                String subject;
                String content;
                Map<String, Object> params;
                mailManager.sendEmail(to = withCapture(), subject = withCapture(), content = withCapture(), params = withCapture());
                assertThat(to, is("test@mail.com"));
                assertThat(subject, is("[重要] テストクリニックから招待状が届きました"));
                assertThat(content, is("invitation1"));
                assertThat(params.get("email"), is("test@mail.com"));
                assertThat(params.get("clinic"), is(notNullValue()));
                assertThat(params.get("user"), is(notNullValue()));
                assertThat(params.get("invitedUser"), is(notNullValue()));
                assertThat(params.get("invitation"), is(notNullValue()));
                assertThat(params.get("activationKey"), is(notNullValue()));
            }};
        }

        @Test
        public void クリニックの招待状が送信されること_ログインユーザが存在しない場合() throws Exception {
            new NonStrictExpectations() {{
                clinicRepository.findOne("10");
                result = Clinic.builder().id("10").name("テストクリニック").description("テストクリニックの説明").build();
                userRepository.findOne("1");
                result = User.builder().id("1").login("login").langKey(LangKey.JAPANESE).build();
                userRepository.findOneByLogin("test@mail.com");
                result = Optional.empty();
            }};

            sut.inviteStaff("10", "1", new HashSet<>(Arrays.asList("test@mail.com")));

            new Verifications() {{
                ClinicInvitation invitation;
                clinicInvitationRepository.save(invitation = withCapture());
                assertThat(invitation.getClinic(), is(notNullValue()));
                assertThat(invitation.getUser(), is(notNullValue()));
                assertThat(invitation.getEmail(), is("test@mail.com"));
                assertThat(invitation.getActivationKey(), is(notNullValue()));

                String to;
                String subject;
                String content;
                Map<String, Object> params;
                mailManager.sendEmail(to = withCapture(), subject = withCapture(), content = withCapture(), params = withCapture());
                assertThat(to, is("test@mail.com"));
                assertThat(subject, is("[重要] テストクリニックから招待状が届きました"));
                assertThat(content, is("invitation2"));
                assertThat(params.get("email"), is("test@mail.com"));
                assertThat(params.get("clinic"), is(notNullValue()));
                assertThat(params.get("user"), is(notNullValue()));
                assertThat(params.get("invitedUser"), is(nullValue()));
                assertThat(params.get("invitation"), is(notNullValue()));
                assertThat(params.get("activationKey"), is(notNullValue()));
            }};
        }
    }

    public static class ActivateTest {

        @Tested
        private ClinicInvitationServiceImpl sut = new ClinicInvitationServiceImpl();
        @Injectable
        private ClinicRepository clinicRepository;
        @Injectable
        private ClinicInvitationRepository clinicInvitationRepository;
        @Injectable
        private ClinicStaffRepository clinicStaffRepository;
        @Injectable
        private UserRepository userRepository;
        @Injectable
        private MailManager mailManager;

        @Test
        public void 招待がアクティベートできること(@Mocked SecurityUtils utils) throws Exception {
            new NonStrictExpectations() {{
                SecurityUtils.getCurrentLogin();
                result = "foo";
                clinicInvitationRepository.findOne("1");
                result = ClinicInvitation.builder()
                    .id("1")
                    .email("")
                    .activationKey("1234567890")
                    .user(User.builder().id("10").login("login").build())
                    .clinic(Clinic.builder().id("100").name("テストクリニック").build()).build();
                userRepository.findOneByLogin("foo");
                result = Optional.of(User.builder().id("1000").login("foo").langKey(LangKey.JAPANESE).build());
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
        public void ログインユーザが存在しない場合はシステムエラーになること(@Mocked SecurityUtils utils) throws Exception {
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
