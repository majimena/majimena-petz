package org.majimena.petz.web.api.invoice;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petz.domain.Invoice;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.InvoiceService;
import org.majimena.petz.web.utils.ErrorsUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.net.URI;
import java.util.List;

/**
 * クリニックのチケットに紐づくインヴォイスのコントローラ.
 */
@RestController
@RequestMapping("/api/v1")
public class ClinicTicketInvoiceController {

    /**
     * インヴォイスサービス.
     */
    @Inject
    private InvoiceService invoiceService;

    /**
     * インヴォイスを取得する.
     *
     * @param clinicId クリニックID
     * @param ticketId チケットID
     * @return レスポンスエンティティ（正常時は200、権限エラーは401、その他は500）
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/tickets/{ticketId}/invoices", method = RequestMethod.GET)
    public ResponseEntity<List<Invoice>> get(@PathVariable String clinicId, @PathVariable String ticketId) {
        // クリニックの権限チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        ErrorsUtils.throwIfNotIdentify(ticketId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // 該当するインヴォイスを取得する
        List<Invoice> invoices = invoiceService.getInvoicesByTicketId(clinicId, ticketId);
        return ResponseEntity.ok().body(invoices);
    }

    /**
     * インヴォイスを作成する.
     *
     * @param clinicId クリニックID
     * @param ticketId チケットID
     * @return レスポンスエンティティ（正常時は201、その他は500）
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/tickets/{ticketId}/invoices", method = RequestMethod.POST)
    public ResponseEntity<Invoice> post(@PathVariable String clinicId, @PathVariable String ticketId) {
        // クリニックの権限チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        ErrorsUtils.throwIfNotIdentify(ticketId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // インヴォイスを作成する
        Invoice invoice = invoiceService.createInvoice(clinicId, ticketId);
        return ResponseEntity
                .created(URI.create("/api/v1/clinics/" + clinicId + "/tickets/" + ticketId + "/invoices/" + invoice.getId()))
                .body(invoice);
    }
}
