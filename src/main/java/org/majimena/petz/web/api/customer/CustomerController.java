package org.majimena.petz.web.api.customer;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petz.domain.Customer;
import org.majimena.petz.domain.customer.CustomerCriteria;
import org.majimena.petz.domain.customer.CustomerRegistry;
import org.majimena.petz.service.CustomerService;
import org.majimena.petz.web.rest.util.PaginationUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
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
public class CustomerController {
    /**
     * 顧客サービス.
     */
    @Inject
    protected CustomerService customerService;

    /**
     * 顧客レジスタバリデータ.
     */
    @Inject
    protected CustomerRegistryValidator customerRegistryValidator;

    /**
     * 顧客サービスを設定する.
     *
     * @param customerService 顧客サービス
     */
    public void setCustomerService(final CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * 顧客レジスタバリデータを設定する.
     *
     * @param customerRegistryValidator 顧客レジスタバリデータ
     */
    public void setCustomerRegistryValidator(final CustomerRegistryValidator customerRegistryValidator) {
        this.customerRegistryValidator = customerRegistryValidator;
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
                                                 @RequestParam(value = "page", required = false) Integer offset, @RequestParam(value = "per_page", required = false) Integer limit,
                                                 @Valid CustomerCriteria criteria) throws URISyntaxException {
        // TODO 権限チェックが必要

        Pageable pageable = PaginationUtil.generatePageRequest(offset, limit);
        criteria.setClinicId(clinicId);

        Page<Customer> users = customerService.getCustomersByCustomerCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(users, "/api/v1/clinics/" + clinicId + "/users", offset, limit);
        return new ResponseEntity<>(users.getContent(), headers, HttpStatus.OK);
    }

    /**
     * 自分のクリニックに新規顧客を登録する.
     *
     * @param clinicId クリニックID
     * @param registry 顧客登録情報
     * @param errors   エラーオブジェクト
     * @return 登録結果
     * @throws BindException バリデーションエラーがある場合に発生する例外
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/customers", method = RequestMethod.POST)
    public ResponseEntity<Void> post(@PathVariable String clinicId, @RequestBody @Valid CustomerRegistry registry, BindingResult errors) throws BindException {
        // TODO 権限チェックが必要

        customerRegistryValidator.validate(registry, errors);
        if (errors.hasErrors()) {
            throw new BindException(errors);
        }

        // 新規顧客を登録する
        Customer customer = customerService.saveCustomer(registry);
        return ResponseEntity.created(
                URI.create("/api/v1/clinics/" + clinicId + "/customers/" + customer.getId())).build();
    }
}
