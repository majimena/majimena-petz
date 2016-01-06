package org.majimena.petz.web.api.user;

import org.majimena.petz.domain.User;
import org.majimena.petz.domain.errors.ErrorCode;
import org.majimena.petz.repository.UserRepository;
import org.majimena.petz.web.api.AbstractValidator;
import org.majimena.petz.web.utils.ErrorsUtils;
import org.springframework.validation.Errors;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Optional;

/**
 * サインアップユーザのバリデータ.
 */
@Named("signupUserValidator")
public class SignupUserValidator extends AbstractValidator<User> {

    /**
     * ユーザリポジトリ.
     */
    @Inject
    private UserRepository userRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validate(Optional<User> target, Errors errors) {
        target.ifPresent(user -> validateLogin(user.getLogin(), errors));
    }

    private void validateLogin(String login, Errors errors) {
        Optional<User> oneByLogin = userRepository.findOneByLogin(login);
        oneByLogin.ifPresent(u -> ErrorsUtils.rejectValue("login", ErrorCode.PTZ_000101, errors));
    }
}
