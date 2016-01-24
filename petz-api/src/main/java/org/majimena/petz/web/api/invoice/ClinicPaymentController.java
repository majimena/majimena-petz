package org.majimena.petz.web.api.invoice;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petz.domain.Payment;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.PaymentService;
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
import java.net.URI;

/**
 * クリニックペイメントコントローラ.
 */
@RestController
@RequestMapping("/api/v1")
public class ClinicPaymentController {

    /**
     * ペイメントサービス.
     */
    @Inject
    private PaymentService paymentService;

    /**
     * ペイメントバリデータ.
     */
    @Inject
    private PaymentValidator paymentValidator;

    /**
     * ペイメントを保存する.
     *
     * @param clinicId  クリニックID
     * @param invoiceId インヴォイスID
     * @return レスポンスエンティティ（正常時は201、入力エラーは400、権限エラーは401、その他は500）
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/invoices/{invoiceId}/payments", method = RequestMethod.POST)
    public ResponseEntity<Payment> post(@PathVariable String clinicId, @PathVariable String invoiceId,
                                        @RequestBody @Valid Payment payment, BindingResult errors) throws BindException {
        // クリニックの権限チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        ErrorsUtils.throwIfNotIdentify(invoiceId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // バリデータを通す
        paymentValidator.validate(payment, errors);
        ErrorsUtils.throwIfHasErrors(errors);

        // 該当するペイメントを取得する
        Payment saved = paymentService.savePayment(payment);
        return ResponseEntity
                .created(URI.create("/api/v1/clinics/" + clinicId + "/invoices/" + invoiceId + "/payments/" + saved.getId()))
                .body(saved);
    }
}
