package org.majimena.petical.web.api.clinics.customers;

import com.codahale.metrics.annotation.Timed;
import org.apache.commons.lang3.StringUtils;
import org.majimena.petical.domain.Customer;
import org.majimena.petical.domain.User;
import org.majimena.petical.security.SecurityUtils;
import org.majimena.petical.service.CustomerService;
import org.majimena.petical.service.UserService;
import org.majimena.petical.web.utils.ErrorsUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * 顧客コントローラ.
 */
@RestController
@RequestMapping("/api/v1")
public class ClinicCustomerController {

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
     * 顧客バリデータ.
     */
    @Inject
    private CustomerValidator customerValidator;

    /**
     * 自分のクリニックの顧客を検索する.
     *
     * @param clinicId クリニックID
     * @return レスポンスエンティティ（通常時は200、入力エラー時は400、認証失敗時は401）
     * @throws URISyntaxException URIエラー
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/customers", method = RequestMethod.GET)
    public ResponseEntity<List<Customer>> getAll(@PathVariable String clinicId) throws URISyntaxException {
        // クリニックの権限チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // 検索する
        List<Customer> customers = customerService.getCustomersByClinicId(clinicId);
        customers.forEach(customer -> customer.setClinic(null));
        return ResponseEntity.ok().body(customers);
    }

    /**
     * 自分のクリニックの顧客（飼い主）を取得する.
     *
     * @param clinicId   クリニックID
     * @param customerId 顧客ID
     * @return レスポンスエンティティ（通常時は200、認証失敗時は401、対象が見つからない場合は404）
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/customers/{customerId}", method = RequestMethod.GET)
    public ResponseEntity<Customer> get(@PathVariable String clinicId, @PathVariable String customerId) {
        // クリニックの権限チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        ErrorsUtils.throwIfNotIdentify(customerId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // 顧客を検索
        return customerService.getCustomerByCustomerId(customerId)
                .filter(customer -> StringUtils.equals(clinicId, customer.getClinic().getId()))
                .map(customer -> ResponseEntity.ok().body(customer))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * 自分のクリニックに新規顧客を登録する.
     *
     * @param clinicId クリニックID
     * @param customer 顧客登録情報
     * @param errors   エラーオブジェクト
     * @return レスポンスエンティティ（通常時は200、入力エラー時は400、認証失敗時は401）
     * @throws BindException バリデーションエラーがある場合に発生する例外
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/customers/validate", method = RequestMethod.POST)
    public ResponseEntity<User> validate(@PathVariable String clinicId,
                                         @RequestBody @Valid Customer customer, BindingResult errors) throws BindException {
        // クリニックの権限チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // 重複ユーザーがいないかチェックする
        String email = customer.getUser().getEmail();
        return userService.getUserByLogin(email)
                .map(user -> ResponseEntity.ok().body(user))
                .orElse(ResponseEntity.ok(customer.getUser()));
    }

    /**
     * 自分のクリニックに新規顧客を登録する.
     *
     * @param clinicId クリニックID
     * @param customer 顧客登録情報
     * @param errors   エラーオブジェクト
     * @return レスポンスエンティティ（通常時は200、入力エラー時は400、認証失敗時は401）
     * @throws BindException バリデーションエラーがある場合に発生する例外
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/customers", method = RequestMethod.POST)
    public ResponseEntity<Customer> post(@PathVariable String clinicId, @RequestParam(required = false) boolean force,
                                         @RequestBody @Valid Customer customer, BindingResult errors) throws BindException {
        // クリニックの権限チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);
        // ユーザーの有無に関わらずに強制的に飼い主を作成する場合は、重複しないログインIDを自動的に採番する
        if (force) {
            customer.getUser().setLogin(System.nanoTime() + "@petical.io");
        }

        // 顧客のデータ整合性チェック
        customer.getClinic().setId(clinicId);
        customerValidator.validate(customer, errors);
        ErrorsUtils.throwIfHasErrors(errors);

        // 新規顧客を登録する
        Customer created = customerService.createCustomerAndUser(customer);
        return ResponseEntity
                .created(URI.create("/api/v1/clinics/" + clinicId + "/customers/" + created.getId()))
                .body(created);
    }

    /**
     * 自分のクリニックに新規顧客を登録する.
     *
     * @param clinicId クリニックID
     * @param customer 顧客登録情報
     * @param errors   エラーオブジェクト
     * @return レスポンスエンティティ（通常時は200、入力エラー時は400、認証失敗時は401）
     * @throws BindException バリデーションエラーがある場合に発生する例外
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/customers", method = RequestMethod.PUT)
    public ResponseEntity<Customer> merge(@PathVariable String clinicId,
                                          @RequestBody @Valid Customer customer, BindingResult errors) throws BindException {
        // クリニックの権限チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // ログインIDで該当するユーザーにマージする形で飼い主登録する
        return userService.getUserByLogin(customer.getUser().getEmail())
                .map(user -> {
                    Customer created = customerService.mergeCustomer(user, customer);
                    return ResponseEntity
                            .created(URI.create("/api/v1/clinics/" + clinicId + "/customers/" + created.getId()))
                            .body(created);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * 自分のクリニックの顧客情報を更新する.
     *
     * @param clinicId   クリニックID
     * @param customerId 顧客ID
     * @param customer   顧客登録情報
     * @param errors     エラーオブジェクト
     * @return レスポンスエンティティ（通常時は200、入力エラー時は400、認証失敗時は401）
     * @throws BindException バリデーションエラーがある場合に発生する例外
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/customers/{customerId}", method = RequestMethod.PUT)
    public ResponseEntity<Customer> put(@PathVariable String clinicId, @PathVariable String customerId,
                                        @RequestBody @Valid Customer customer, BindingResult errors) throws BindException {
        // クリニックの権限チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        ErrorsUtils.throwIfNotIdentify(customerId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // 顧客のデータ整合性チェック
        customer.setId(customerId);
        customer.getClinic().setId(clinicId);
        customerValidator.validate(customer, errors);
        ErrorsUtils.throwIfHasErrors(errors);

        // 顧客を更新する
        Customer saved = customerService.updateCustomer(customer);
        return ResponseEntity.ok().body(saved);
    }

    /**
     * 顧客情報を削除する.
     *
     * @param clinicId   クリニックID
     * @param customerId 顧客ID
     * @return レスポンスエンティティ（通常時は200、認証失敗時は401、対象が見つからない場合は404）
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/customers/{customerId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable String clinicId, @PathVariable String customerId) {
        // クリニックの権限チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        ErrorsUtils.throwIfNotIdentify(customerId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // 顧客を削除する
        return customerService.getCustomerByCustomerId(customerId)
                .filter(customer -> StringUtils.equals(clinicId, customer.getClinic().getId()))
                .map(customer -> {
                    customerService.deleteCustomer(customer);
                    return ResponseEntity.ok().build();
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
