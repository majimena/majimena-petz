package org.majimena.petz.web.api.customer;

import com.codahale.metrics.annotation.Timed;
import org.apache.commons.lang3.StringUtils;
import org.majimena.petz.common.exceptions.ResourceCannotAccessException;
import org.majimena.petz.domain.Customer;
import org.majimena.petz.domain.customer.CustomerCriteria;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.CustomerService;
import org.majimena.petz.web.rest.util.PaginationUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * 顧客コントローラ.
 */
@RestController
@RequestMapping("/api/v1")
public class CustomerController {
    /**
     * 顧客サービス.
     */
    @Inject
    protected CustomerService customerService;

    /**
     * 顧客バリデータ.
     */
    @Inject
    protected CustomerValidator customerValidator;

    /**
     * 顧客サービスを設定する.
     *
     * @param customerService 顧客サービス
     */
    public void setCustomerService(final CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * 顧客バリデータを設定する.
     *
     * @param customerValidator 顧客レジスタバリデータ
     */
    public void setCustomerValidator(final CustomerValidator customerValidator) {
        this.customerValidator = customerValidator;
    }

    /**
     * 自分のクリニックの顧客を検索する.
     *
     * @param clinicId クリニックID
     * @param offset   検索時のオフセット値
     * @param limit    検索結果数の上限値
     * @param criteria 検索条件
     * @return 検索結果
     * @throws URISyntaxException URIエラー
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/customers", method = RequestMethod.GET)
    public ResponseEntity<List<Customer>> getAll(@PathVariable String clinicId,
                                                 @RequestParam(value = "page", required = false) Integer offset,
                                                 @RequestParam(value = "per_page", required = false) Integer limit,
                                                 @Valid CustomerCriteria criteria) throws URISyntaxException {
        // クリニックの権限チェック
        if (!SecurityUtils.isUserInClinic(clinicId)) {
            throw new ResourceCannotAccessException(); // FIXME メッセージ詰める
        }

        Pageable pageable = PaginationUtil.generatePageRequest(offset, limit);
        criteria.setClinicId(clinicId);

        Page<Customer> users = customerService.getCustomersByCustomerCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(users, "/api/v1/clinics/" + clinicId + "/users", offset, limit);
        return new ResponseEntity<>(users.getContent(), headers, HttpStatus.OK);
    }

    /**
     * 自分のクリニックの顧客（飼い主）を取得する.
     *
     * @param clinicId   クリニックID
     * @param customerId 顧客ID
     * @return 該当する顧客
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/customers/{customerId}", method = RequestMethod.GET)
    public ResponseEntity<Customer> get(@PathVariable String clinicId, @PathVariable String customerId) {
        // クリニックの権限チェック
        if (!SecurityUtils.isUserInClinic(clinicId)) {
            throw new ResourceCannotAccessException(); // FIXME メッセージ詰める
        }

        Optional<Customer> result = customerService.getCustomerByCustomerId(customerId);
        return result
            .map(customer -> {
                if (!StringUtils.equals(clinicId, customer.getClinic().getId())) {
                    throw new ResourceCannotAccessException(); // FIXME メッセージ詰める
                }
                return ResponseEntity.ok().body(customer);
            })
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * 自分のクリニックに新規顧客を登録する.
     *
     * @param clinicId クリニックID
     * @param customer 顧客登録情報
     * @param errors   エラーオブジェクト
     * @return 登録結果
     * @throws BindException バリデーションエラーがある場合に発生する例外
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/customers", method = RequestMethod.POST)
    public ResponseEntity<Customer> post(@PathVariable String clinicId,
                                         @RequestBody @Valid Customer customer, BindingResult errors) throws BindException {
        // クリニックの権限チェック
        if (!SecurityUtils.isUserInClinic(clinicId)) {
            throw new ResourceCannotAccessException(); // FIXME メッセージ詰める
        }

        // 顧客のデータ整合性チェック
        customerValidator.validate(customer, errors);
        if (errors.hasErrors()) {
            throw new BindException(errors);
        }

        // 新規顧客を登録する
        Customer created = customerService.saveCustomer(clinicId, customer);
        return ResponseEntity.created(
            URI.create("/api/v1/clinics/" + clinicId + "/customers/" + created.getId())).body(created);
    }

    /**
     * 自分のクリニックの顧客情報を更新する.
     *
     * @param clinicId   クリニックID
     * @param customerId 顧客ID
     * @param customer   顧客登録情報
     * @param errors     エラーオブジェクト
     * @return 登録結果
     * @throws BindException バリデーションエラーがある場合に発生する例外
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/customers/{customerId}", method = RequestMethod.PUT)
    public ResponseEntity<Customer> put(@PathVariable String clinicId, @PathVariable String customerId,
                                        @RequestBody @Valid Customer customer, BindingResult errors) throws BindException {
        customer.setId(customerId);
        ResponseEntity<Customer> post = post(clinicId, customer, errors);
        return ResponseEntity.ok().body(post.getBody());
    }
}
