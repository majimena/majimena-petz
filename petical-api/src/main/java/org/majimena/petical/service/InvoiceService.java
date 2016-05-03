package org.majimena.petical.service;

import org.majimena.petical.domain.Invoice;
import org.majimena.petical.domain.ticket.InvoiceCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
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
     * @param clinicId クリニックID
     * @param ticketId チケットID
     * @return 該当するインヴォイス
     */
    List<Invoice> getInvoicesByTicketId(String clinicId, String ticketId);

    /**
     * インヴォイスを取得する.
     *
     * @param invoiceId インヴォイスID
     * @return 該当するインヴォイス
     */
    Optional<Invoice> getInvoiceByInvoiceId(String invoiceId);

    /**
     * インヴォイスを新規作成する.
     *
     * @param clinicId クリニックID
     * @param ticketId チケットID
     * @return 作成したインヴォイス
     */
    Invoice createInvoice(String clinicId, String ticketId);

    /**
     * インヴォイスをキャンセルする.
     *
     * @param clinicId  クリニックID
     * @param invoiceId インヴォイスID
     */
    void cancelInvoice(String clinicId, String invoiceId);
}
