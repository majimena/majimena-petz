package org.majimena.petical.web.api.user;

import mockit.Injectable;
import mockit.NonStrictExpectations;
import mockit.Tested;
import org.junit.Test;
import org.majimena.petical.domain.User;
import org.majimena.petical.domain.user.SignupRegistry;
import org.majimena.petical.repository.UserRepository;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @see SignupRegistryValidator
 */
public class SignupRegistryValidatorTest {

    @Tested
    private SignupRegistryValidator sut = new SignupRegistryValidator();
    @Injectable
    private UserRepository userRepository;

    private static SignupRegistry newSignupRegistry() {
        return SignupRegistry.builder()
                .firstName("12345678901234567890123456789012345678901234567890")
                .lastName("12345678901234567890123456789012345678901234567890")
                .email("example1@abcdefghij.com")
                .password("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890")
                .build();
    }

    @Test
    public void 対象が指定されていなくてもエラーにならないこと() throws Exception {
        SignupRegistry data = newSignupRegistry();
        Errors errors = new BindException(data, "signup");

        sut.validate(Optional.ofNullable(null), errors);

        assertThat(errors, is(notNullValue()));
        assertThat(errors.hasErrors(), is(false));
    }

    @Test
    public void 正常時はエラーにならないこと() throws Exception {
        SignupRegistry data = newSignupRegistry();
        Errors errors = new BindException(data, "signup");

        new NonStrictExpectations() {{
            userRepository.findOneByLogin("example1@abcdefghij.com");
            result = Optional.empty();
        }};

        sut.validate(Optional.of(data), errors);

        assertThat(errors, is(notNullValue()));
        assertThat(errors.hasErrors(), is(false));
    }

    @Test
    public void ログインIDが重複している場合はエラーになること() throws Exception {
        SignupRegistry data = newSignupRegistry();
        Errors errors = new BindException(data, "signup");

        new NonStrictExpectations() {{
            userRepository.findOneByLogin("example1@abcdefghij.com");
            result = Optional.of(User.builder().id("1").build());
        }};

        sut.validate(Optional.of(data), errors);

        assertThat(errors, is(notNullValue()));
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getAllErrors().size(), is(1));
        assertThat(errors.getAllErrors().get(0).getCode(), is("PTZ_000101"));
    }
}
