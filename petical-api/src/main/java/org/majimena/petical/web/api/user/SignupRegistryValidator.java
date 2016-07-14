package org.majimena.petical.web.api.user;

import org.majimena.petical.domain.User;
import org.majimena.petical.domain.errors.ErrorCode;
import org.majimena.petical.domain.user.SignupRegistry;
import org.majimena.petical.repository.UserRepository;
import org.majimena.petical.web.api.AbstractValidator;
import org.majimena.petical.web.utils.ErrorsUtils;
import org.springframework.validation.Errors;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Optional;

/**
 * サインアップレジストリのバリデータ.
 */
@Named("signupRegistryValidator")
public class SignupRegistryValidator extends AbstractValidator<SignupRegistry> {

    /**
     * ユーザリポジトリ.
     */
    @Inject
    private UserRepository userRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validate(Optional<SignupRegistry> target, Errors errors) {
        target.ifPresent(registry -> {
            validateLogin(registry.getEmail(), errors);
        });
    }

    private void validateLogin(String login, Errors errors) {
        Optional<User> one = userRepository.findOneByActivatedIsTrueAndLogin(login);
        one.ifPresent(u -> ErrorsUtils.rejectValue("email", ErrorCode.PTZ_000101, errors));
    }
}
