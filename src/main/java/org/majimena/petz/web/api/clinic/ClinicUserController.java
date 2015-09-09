package org.majimena.petz.web.api.clinic;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petz.domain.Customer;
import org.majimena.petz.domain.customer.CustomerAuthorizationToken;
import org.majimena.petz.service.CustomerService;
import org.springframework.http.ResponseEntity;
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
    protected CustomerService customerService;

    /**
     * 顧客サービスを設定する.
     *
     * @param customerService 顧客サービス
     */
    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * 既存ユーザーをクリニックに紐付けして顧客として登録する.
     *
     * @param token 顧客認証トークン
     * @return なし
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/users/{userId}", method = RequestMethod.POST)
    public ResponseEntity<Customer> authorize(@Valid @RequestBody CustomerAuthorizationToken token) {
        // TODO クリニックの権限チェックが必要

        // 電話番号で認証して、クリニックと紐付けする
        Customer customer = customerService.authorize(token);
        return ResponseEntity.ok().body(customer);
    }
}
