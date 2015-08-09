package org.majimena.petz.web.api.user;

import org.majimena.petz.common.exceptions.ApplicationException;
import org.majimena.petz.domain.errors.ErrorCode;
import org.majimena.petz.domain.user.SignupRegistry;
import org.majimena.petz.repository.UserRepository;
import org.majimena.petz.web.api.AbstractValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Optional;

/**
 * Created by todoken on 2015/07/13.
 */
@Named("signupRegistryValidator")
public class SignupRegistryValidator extends AbstractValidator<SignupRegistry> {

    @Inject
    private UserRepository userRepository;

    @Override
    protected void validate(Optional<SignupRegistry> target, Errors errors) {
        target.ifPresent(registry -> {
            userRepository.findOneByLogin(registry.getEmail()).ifPresent(u -> {
                throw new ApplicationException(ErrorCode.PTZ_000101);
            });
        });
    }
}
