package org.majimena.petz.web.api.customer;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petz.domain.Customer;
import org.majimena.petz.domain.User;
import org.majimena.petz.domain.errors.ErrorCode;
import org.majimena.petz.repository.UserRepository;
import org.majimena.petz.web.api.AbstractValidator;
import org.springframework.validation.Errors;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Optional;

/**
 * 顧客バリデータ.
 */
@Named("customerValidator")
public class CustomerValidator extends AbstractValidator<Customer> {

    /**
     * ユーザーリポジトリ.
     */
    @Inject
    private UserRepository userRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validate(Optional<Customer> target, Errors errors) {
        target.ifPresent(c -> {
            // 変更しようとしているユーザー以外のユーザーのログインIDと重複していたらエラーにする
            Optional<User> user = userRepository.findOneByLogin(c.getUser().getLogin());
            if (!StringUtils.equals(user.map(u -> u.getId()).orElse(""), c.getUser().getId())) {
                errors.reject(ErrorCode.PTZ_000101.name());
            }
        });
    }
}
