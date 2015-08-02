package org.majimena.petz.web.api.user;

import org.majimena.petz.domain.User;
import org.majimena.petz.domain.errors.ErrorCode;
import org.majimena.petz.domain.user.UserPatchRegistry;
import org.majimena.petz.repository.UserRepository;
import org.majimena.petz.web.api.AbstractValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import javax.inject.Inject;
import java.util.Optional;

/**
 * Created by todoken on 2015/07/13.
 */
@Component
public class UserPatchRegistryValidator extends AbstractValidator<UserPatchRegistry> {

    @Inject
    private UserRepository userRepository;

    @Override
    protected void validate(Optional<UserPatchRegistry> target, Errors errors) {
        target.ifPresent(registry -> {
            Optional<User> oneByLogin = userRepository.findOneByLogin(registry.getEmail());
            oneByLogin.ifPresent(u -> errors.rejectValue("email", ErrorCode.PTZ_000101.name()));
        });
    }
}
