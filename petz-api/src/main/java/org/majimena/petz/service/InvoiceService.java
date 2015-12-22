package org.majimena.petz.service;

import org.majimena.petz.domain.Invoice;
import org.majimena.petz.domain.ticket.InvoiceCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * インヴォイスサービス.
 */
public interface InvoiceService {

    /**
     * インヴォイスを検索する.
     *
     * @param criteria インヴォイスクライテリア
     * @param pageable ページング条件
     * @return 検索したインヴォイス
     */
    Page<Invoice> findInvoicesByInvoiceCriteria(InvoiceCriteria criteria, Pageable pageable);

    /**
     * インヴォイスを取得する.
     *
     * @param clinicId  クリニックID
     * @param ticketId  チケットID
     * @param invoiceId インヴォイスID
     * @return 該当するインヴォイス
     */
    Optional<Invoice> getInvoiceByInvoiceId(String clinicId, String ticketId, String invoiceId);

    /**
     * インヴォイスを新規作成する.
     *
     * @param clinicId クリニックID
     * @param ticketId チケットID
     * @return 作成したインヴォイス
     */
    Invoice createInvoice(String clinicId, String ticketId);
}
