package org.majimena.petz.web.api.ticket;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petz.domain.Invoice;
import org.majimena.petz.domain.ticket.InvoiceCriteria;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.InvoiceService;
import org.majimena.petz.web.utils.ErrorsUtils;
import org.majimena.petz.web.utils.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * インヴォイスコントローラ.
 */
@RestController
@RequestMapping("/api/v1")
public class InvoiceController {

    /**
     * インヴォイスサービス.
     */
    @Inject
    private InvoiceService invoiceService;

    /**
     * インヴォイスを検索する.
     *
     * @param clinicId クリニックID
     * @return レスポンスエンティティ（正常時は200、その他は500）
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/tickets/{ticketId}/invoices", method = RequestMethod.GET)
    public ResponseEntity<List<Invoice>> get(@PathVariable String clinicId, @PathVariable String ticketId, @Valid InvoiceCriteria criteria,
                                             @RequestParam(value = "page", required = false) Integer offset,
                                             @RequestParam(value = "per_page", required = false) Integer limit) {
        // クリニックの権限チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        ErrorsUtils.throwIfNotIdentify(ticketId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // インヴォイスを検索する
        criteria.setClinicId(clinicId);
        Pageable pageable = PaginationUtils.generatePageRequest(offset, limit);
        Page<Invoice> page = invoiceService.findInvoicesByInvoiceCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtils.generatePaginationHttpHeaders(page, "/api/v1/clinics/" + clinicId + "/tickets/" + ticketId + "/invoices", offset, limit, criteria);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * インヴォイスを取得する.
     * @param clinicId クリニックID
     * @param ticketId チケットID
     * @param invoiceId インヴォイスID
     * @return レスポンスエンティティ（正常時は200、該当がない場合は404、その他は500）
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/tickets/{ticketId}/invoices/{invoiceId}", method = RequestMethod.GET)
    public ResponseEntity<Invoice> get(@PathVariable String clinicId, @PathVariable String ticketId, @PathVariable String invoiceId) {
        // クリニックの権限チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        ErrorsUtils.throwIfNotIdentify(ticketId);
        ErrorsUtils.throwIfNotIdentify(invoiceId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // 該当するインヴォイスを取得する
        Optional<Invoice> invoice = invoiceService.getInvoiceByInvoiceId(clinicId, ticketId, invoiceId);
        return invoice
                .map(p -> ResponseEntity.ok().body(p))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
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
    public ResponseEntity<Invoice> put(@PathVariable String clinicId, @PathVariable String ticketId) {
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
