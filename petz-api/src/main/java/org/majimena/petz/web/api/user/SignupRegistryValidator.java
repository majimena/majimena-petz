package org.majimena.petz.web.api.user;

import org.majimena.petz.domain.User;
import org.majimena.petz.domain.errors.ErrorCode;
import org.majimena.petz.domain.user.SignupRegistry;
import org.majimena.petz.repository.UserRepository;
import org.majimena.petz.web.api.AbstractValidator;
import org.majimena.petz.web.utils.ErrorsUtils;
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
        Optional<User> one = userRepository.findOneByLogin(login);
        one.ifPresent(u -> ErrorsUtils.rejectValue("email", ErrorCode.PTZ_000101, errors));
    }
}
