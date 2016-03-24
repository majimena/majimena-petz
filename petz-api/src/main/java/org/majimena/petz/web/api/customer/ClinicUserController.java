package org.majimena.petz.web.api.customer;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petz.domain.Customer;
import org.majimena.petz.domain.User;
import org.majimena.petz.domain.customer.CustomerAuthenticationToken;
import org.majimena.petz.security.ResourceCannotAccessException;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.CustomerService;
import org.majimena.petz.service.UserService;
import org.majimena.petz.web.utils.ErrorsUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class ClinicUserController {

    /**
     * 顧客サービス.
     */
    @Inject
    private CustomerService customerService;

    /**
     * ユーザーサービス.
     */
    @Inject
    private UserService userService;

    /**
     * 顧客認証トークンのバリデータ.
     */
    @Inject
    private CustomerAuthenticationTokenValidator customerAuthenticationTokenValidator;

    /**
     * ユーザーを認証して、ユーザー情報を取得する.
     *
     * @param clinicId クリニックID
     * @param token    顧客認証トークン
     * @param errors   エラー
     * @return レスポンスエンティティ（通常時は200、入力エラー時は400、認証失敗時は401）
     * @throws BindException バリデーションエラーがあった場合に発生する例外（レスポンスコードは400）
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/users/authenticate", method = RequestMethod.POST)
    public ResponseEntity<User> authenticate(@PathVariable String clinicId, @Valid @RequestBody CustomerAuthenticationToken token, BindingResult errors) throws BindException {
        // クリニックの権限チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);
        token.setClinicId(clinicId);

        // カスタムバリデーション
        customerAuthenticationTokenValidator.validate(token, errors);
        ErrorsUtils.throwIfHasErrors(errors);

        // ユーザーを取得する
        return userService.getUserByLogin(token.getLogin())
                .map(user -> ResponseEntity.ok().body(user))
                .orElseThrow(() -> new ResourceCannotAccessException("Cannot access resource."));
    }

    /**
     * ユーザーを認証して、ユーザーをクリニックの顧客として登録する.
     *
     * @param clinicId クリニックID
     * @param token    顧客認証トークン
     * @param errors   エラー
     * @return レスポンスエンティティ（通常時は200、入力エラー時は400、認証失敗時は401）
     * @throws BindException バリデーションエラーがあった場合に発生する例外（レスポンスコードは400）
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/users/import", method = RequestMethod.POST)
    public ResponseEntity<Customer> imp(@PathVariable String clinicId, @Valid @RequestBody CustomerAuthenticationToken token, BindingResult errors) throws BindException {
        // クリニックの権限チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);
        token.setClinicId(clinicId);

        // カスタムバリデーション
        customerAuthenticationTokenValidator.validate(token, errors);
        ErrorsUtils.throwIfHasErrors(errors);

        // クリニックとユーザーをひも付けする
        Customer customer = customerService.saveCustomer(token);
        return ResponseEntity.ok().body(customer);
    }
}
