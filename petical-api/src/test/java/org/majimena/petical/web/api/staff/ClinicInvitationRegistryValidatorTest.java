package org.majimena.petical.web.api.staff;

import mockit.Injectable;
import mockit.NonStrictExpectations;
import mockit.Tested;
import org.junit.Test;
import org.majimena.petical.domain.ClinicStaff;
import org.majimena.petical.domain.User;
import org.majimena.petical.domain.clinic.ClinicInvitationRegistry;
import org.majimena.petical.repository.ClinicStaffRepository;
import org.majimena.petical.repository.UserRepository;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @see ClinicInvitationRegistryValidator
 */
public class ClinicInvitationRegistryValidatorTest {

    @Tested
    private ClinicInvitationRegistryValidator sut = new ClinicInvitationRegistryValidator();

    @Injectable
    private UserRepository userRepository;

    @Injectable
    private ClinicStaffRepository clinicStaffRepository;

    protected static ClinicInvitationRegistry newClinicInvitationRegistry() {
        return ClinicInvitationRegistry.builder()
                .clinicId("1")
                .emails(new String[]{"foo@bar.com"})
                .build();
    }

    @Test
    public void 対象が指定されていなくてもエラーにならないこと() throws Exception {
        ClinicInvitationRegistry data = newClinicInvitationRegistry();
        Errors errors = new BindException(data, "clinicInvitationRegistry");

        sut.validate(Optional.ofNullable(null), errors);

        assertThat(errors, is(notNullValue()));
        assertThat(errors.hasErrors(), is(false));
    }

    @Test
    public void 正常時はエラーにならないこと() throws Exception {
        ClinicInvitationRegistry data = newClinicInvitationRegistry();
        Errors errors = new BindException(data, "clinicInvitationRegistry");

        new NonStrictExpectations() {{
            userRepository.findOneByActivatedIsTrueAndLogin("foo@bar.com");
            result = Optional.of(User.builder().id("user1").login("foo@bar.com").build());
            clinicStaffRepository.findByClinicIdAndUserId("1", "user1");
            result = Optional.ofNullable(null);
        }};

        sut.validate(Optional.of(data), errors);

        assertThat(errors, is(notNullValue()));
        assertThat(errors.hasErrors(), is(false));
    }

    @Test
    public void メールアドレスではない場合はリジェクトされること() throws Exception {
        ClinicInvitationRegistry data = newClinicInvitationRegistry();
        Errors errors = new BindException(data, "clinicInvitationRegistry");

        data.setEmails(new String[]{"foo.at.bar.com"});
        sut.validate(Optional.of(data), errors);

        assertThat(errors, is(notNullValue()));
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getFieldErrors().size(), is(1));
        assertThat(errors.getFieldErrors().get(0).getField(), is("emails"));
        assertThat(errors.getFieldErrors().get(0).getCode(), is("errors.validation.email"));
    }

    @Test
    public void 既に使用されているログインIDが指定されている場合はリジェクトされること() throws Exception {
        ClinicInvitationRegistry data = newClinicInvitationRegistry();
        Errors errors = new BindException(data, "clinicInvitationRegistry");

        new NonStrictExpectations() {{
            userRepository.findOneByActivatedIsTrueAndLogin("foo@bar.com");
            result = Optional.of(User.builder().id("user1").login("foo@bar.com").build());
            clinicStaffRepository.findByClinicIdAndUserId("1", "user1");
            result = Optional.of(ClinicStaff.builder().id("staff1").build());
        }};

        sut.validate(Optional.of(data), errors);

        assertThat(errors, is(notNullValue()));
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getFieldErrors().size(), is(1));
        assertThat(errors.getFieldErrors().get(0).getField(), is("emails"));
        assertThat(errors.getFieldErrors().get(0).getCode(), is("PTZ_001204"));
    }
}
