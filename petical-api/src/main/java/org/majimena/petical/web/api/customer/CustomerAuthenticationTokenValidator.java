package org.majimena.petical.web.api.customer;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petical.domain.User;
import org.majimena.petical.domain.customer.CustomerAuthenticationToken;
import org.majimena.petical.domain.errors.ErrorCode;
import org.majimena.petical.repository.CustomerRepository;
import org.majimena.petical.repository.UserRepository;
import org.majimena.petical.web.api.AbstractValidator;
import org.majimena.petical.web.utils.ErrorsUtils;
import org.springframework.validation.Errors;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Optional;

/**
 * 顧客認証トークンのバリデータ.
 */
@Named("customerAuthenticationTokenValidator")
public class CustomerAuthenticationTokenValidator extends AbstractValidator<CustomerAuthenticationToken> {

    /**
     * ユーザリポジトリ.
     */
    @Inject
    private UserRepository userRepository;

    /**
     * 顧客リポジトリ.
     */
    @Inject
    private CustomerRepository customerRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validate(Optional<CustomerAuthenticationToken> target, Errors errors) {
        target.ifPresent(token -> {
            validateLogin(token, errors);
        });
    }

    private User validateLogin(CustomerAuthenticationToken token, Errors errors) {
        // ログインIDで検索
        return userRepository.findOneByLogin(token.getLogin())
                .map(user -> {
                    // 氏名、電話番号が同じかチェック
                    ErrorsUtils.rejectIfNotEquals(ErrorCode.PTZ_000201, token.getFirstName(), user.getFirstName(), errors);
                    ErrorsUtils.rejectIfNotEquals(ErrorCode.PTZ_000201, token.getLastName(), user.getLastName(), errors);
                    validatePhoneNo(token.getPhoneNo(), user.getPhoneNo(), user.getMobilePhoneNo(), errors);
                    // 既に顧客登録されていないかチェック
                    validateUniqueUserId(token.getClinicId(), user.getId(), errors);
                    return user;
                })
                .orElseGet(() -> {
                    // ユーザーがいなければエラー
                    ErrorsUtils.reject(ErrorCode.PTZ_000999, errors);
                    return null;
                });
    }

    private void validatePhoneNo(String phoneNo, String no1, String no2, Errors errors) {
        // 自宅電話番号でも携帯電話番号でもない場合はエラーにする
        if (!StringUtils.equals(phoneNo, no1) && !StringUtils.equals(phoneNo, no2)) {
            ErrorsUtils.reject(ErrorCode.PTZ_000201, errors);
        }
    }

    private void validateUniqueUserId(String clinicId, String userId, Errors errors) {
        // 顧客はクリニックIDとユーザIDでユニークでなければならない
        customerRepository.findByClinicIdAndUserId(clinicId, userId)
                .ifPresent(customer -> ErrorsUtils.reject(ErrorCode.PTZ_000101, errors));
    }
}
