package org.majimena.petz.web.api.customer;

import org.majimena.petz.domain.Customer;
import org.majimena.petz.domain.errors.ErrorCode;
import org.majimena.petz.repository.CustomerRepository;
import org.majimena.petz.repository.UserRepository;
import org.majimena.petz.web.api.AbstractValidator;
import org.majimena.petz.web.utils.ErrorsUtils;
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
     * 顧客リポジトリ.
     */
    @Inject
    private CustomerRepository customerRepository;

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
        target.ifPresent(customer -> {
            // 顧客IDが指定されている場合は該当データが存在するかチェック
            validateId(Optional.ofNullable(customer.getId()), customer.getClinic().getId(), errors);
            // 変更しようとしているユーザー以外のユーザーのログインIDと重複していたらエラーにする
            validateLogin(Optional.ofNullable(customer.getId()), customer.getUser().getLogin(), errors);
        });
    }

    private void validateId(Optional<String> value, String clinicId, Errors errors) {
        value.ifPresent(id -> {
            Customer one = customerRepository.findOne(id);
            if (one == null) {
                ErrorsUtils.reject(ErrorCode.PTZ_999998, errors);
            } else {
                ErrorsUtils.throwIfNotEqual(clinicId, one.getClinic().getId());
            }
        });
    }

    private void validateLogin(Optional<String> value, String login, Errors errors) {
        value.orElseGet(() -> {
            userRepository.findOneByLogin(login).ifPresent(user -> ErrorsUtils.reject(ErrorCode.PTZ_000101, errors));
            return null;
        });
    }
}
