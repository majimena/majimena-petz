package org.majimena.petz.web.api.clinic;

import mockit.Injectable;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Tested;
import org.junit.Test;
import org.majimena.petz.domain.ClinicInvitation;
import org.majimena.petz.domain.User;
import org.majimena.petz.domain.clinic.ClinicInvitationAcception;
import org.majimena.petz.repository.ClinicInvitationRepository;
import org.majimena.petz.security.SecurityUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @see ClinicInvitationAcceptionValidator
 */
public class ClinicInvitationAcceptionValidatorTest {

    @Tested
    private ClinicInvitationAcceptionValidator sut = new ClinicInvitationAcceptionValidator();
    @Injectable
    private ClinicInvitationRepository clinicInvitationRepository;
    @Mocked
    private SecurityUtils securityUtils;

    protected static ClinicInvitationAcception newClinicInvitationAcception() {
        return ClinicInvitationAcception.builder()
                .clinicId("1")
                .clinicInvitationId("invitation1")
                .activationKey("1234567890")
                .build();
    }

    @Test
    public void 対象が指定されていなくてもエラーにならないこと() throws Exception {
        ClinicInvitationAcception data = newClinicInvitationAcception();
        Errors errors = new BindException(data, "clinicInvitationAcception");

        sut.validate(Optional.ofNullable(null), errors);

        assertThat(errors, is(notNullValue()));
        assertThat(errors.hasErrors(), is(false));
    }

    @Test
    public void 正常時はエラーにならないこと_既存ユーザを招待する場合() throws Exception {
        ClinicInvitationAcception data = newClinicInvitationAcception();
        Errors errors = new BindException(data, "clinicInvitationAcception");

        new NonStrictExpectations() {{
            clinicInvitationRepository.findOne("invitation1");
            result = ClinicInvitation.builder()
                .user(User.builder().id("user1").build())
                .invitedUser(User.builder().id("exist1").build())
                .build();
            SecurityUtils.getCurrentUserId();
            result = "exist1";
        }};

        sut.validate(Optional.of(data), errors);

        assertThat(errors, is(notNullValue()));
        assertThat(errors.hasErrors(), is(false));
    }

    @Test
    public void 正常時はエラーにならないこと_新規ユーザを招待する場合() throws Exception {
        ClinicInvitationAcception data = newClinicInvitationAcception();
        Errors errors = new BindException(data, "clinicInvitationAcception");

        new NonStrictExpectations() {{
            clinicInvitationRepository.findOne("invitation1");
            result = ClinicInvitation.builder().user(User.builder().id("user1").build()).build();
            SecurityUtils.getCurrentUserId();
            result = "exist1";
        }};

        sut.validate(Optional.of(data), errors);

        assertThat(errors, is(notNullValue()));
        assertThat(errors.hasErrors(), is(false));
    }

    @Test
    public void 招待状がない場合はリジェクトされること() throws Exception {
        ClinicInvitationAcception data = newClinicInvitationAcception();
        Errors errors = new BindException(data, "clinicInvitationAcception");

        new NonStrictExpectations() {{
            clinicInvitationRepository.findOne("invitation1");
            result = null;
        }};

        sut.validate(Optional.of(data), errors);

        assertThat(errors, is(notNullValue()));
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getAllErrors().size(), is(1));
        assertThat(errors.getGlobalError(), is(notNullValue()));
        assertThat(errors.getGlobalError().getCode(), is("PTZ_001201"));
    }

    @Test
    public void 自分あての招待状でない場合はリジェクトされること() throws Exception {
        ClinicInvitationAcception data = newClinicInvitationAcception();
        Errors errors = new BindException(data, "clinicInvitationAcception");

        new NonStrictExpectations() {{
            clinicInvitationRepository.findOne("invitation1");
            result = ClinicInvitation.builder()
                .user(User.builder().id("999").build())
                .invitedUser(User.builder().id("invited1").build())
                .build();
            SecurityUtils.getCurrentUserId();
            result = "invited2";
        }};

        sut.validate(Optional.of(data), errors);

        assertThat(errors, is(notNullValue()));
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getAllErrors().size(), is(1));
        assertThat(errors.getGlobalError(), is(notNullValue()));
        assertThat(errors.getGlobalError().getCode(), is("PTZ_001202"));
    }
}
