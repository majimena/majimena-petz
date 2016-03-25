package org.majimena.petz.service.impl;

import org.majimena.petz.common.utils.ExceptionUtils;
import org.majimena.petz.datatype.TicketState;
import org.majimena.petz.domain.Examination;
import org.majimena.petz.domain.Invoice;
import org.majimena.petz.domain.Ticket;
import org.majimena.petz.domain.ticket.InvoiceCriteria;
import org.majimena.petz.repository.ExaminationRepository;
import org.majimena.petz.repository.InvoiceRepository;
import org.majimena.petz.repository.TicketRepository;
import org.majimena.petz.repository.spec.InvoiceSpecs;
import org.majimena.petz.service.InvoiceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * インヴォイスサービスの実装.
 */
@Service
public class InvoiceServiceImpl implements InvoiceService {

    /**
     * インヴォイスリポジトリ.
     */
    @Inject
    private InvoiceRepository invoiceRepository;

    /**
     * チケットリポジトリ.
     */
    @Inject
    private TicketRepository ticketRepository;

    /**
     * 診察リポジトリ.
     */
    @Inject
    private ExaminationRepository examinationRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Invoice> findInvoicesByInvoiceCriteria(InvoiceCriteria criteria, Pageable pageable) {
        Page<Invoice> page = invoiceRepository.findAll(InvoiceSpecs.of(criteria), pageable);
        // lazy load
        page.forEach(invoice -> {
            invoice.getTicket().getId();
            invoice.getTicket().getChart().getId();
            invoice.getTicket().getChart().getCustomer().getId();
            invoice.getTicket().getChart().getCustomer().getUser().getId();
        });
        return page;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Invoice> getInvoicesByTicketId(String clinicId, String ticketId) {
        InvoiceCriteria criteria = new InvoiceCriteria();
        criteria.setClinicId(clinicId);
        criteria.setTicketId(ticketId);

        List<Invoice> invoices = invoiceRepository.findAll(InvoiceSpecs.of(criteria));
        invoices.forEach(invoice -> invoice.getTicket().getId()); // lazy load
        return invoices;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Invoice> getInvoiceByInvoiceId(String invoiceId) {
        Invoice one = invoiceRepository.findOne(invoiceId);
        if (one != null) {
            one.getTicket().getId(); // lazy load
        }
        return Optional.ofNullable(one);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Invoice createInvoice(String clinicId, String ticketId) {
        // チケットが存在するか確認する
        Ticket ticket = ticketRepository.findOne(ticketId);
        ExceptionUtils.throwIfNull(ticket);
        ExceptionUtils.throwIfNotEqual(clinicId, ticket.getClinic().getId());

        // チケット自体のステータスを支払中にする
        ticket.setState(TicketState.PAYMENT);
        ticketRepository.save(ticket);

        // 請求額を計算する
        List<Examination> examinations = examinationRepository.findByTicketId(ticketId);
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal tax = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;
        for (Examination examination : examinations) {
            subtotal = subtotal.add(examination.getTotal().subtract(examination.getTax()));
            tax = tax.add(examination.getTax());
            total = total.add(examination.getTotal());
        }

        // インヴォイスを作成する
        Invoice invoice = new Invoice();
        invoice.setTicket(ticket);
        invoice.setSubtotal(subtotal);
        invoice.setTax(tax);
        invoice.setTotal(total);
        invoice.setReceiptAmount(BigDecimal.ZERO);
        invoice.setPaid(Boolean.FALSE);
        invoice.setRemoved(Boolean.FALSE);
        return invoiceRepository.save(invoice);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void cancelInvoice(String clinicId, String invoiceId) {
        // キャンセルするインヴォイスを取得する
        Invoice one = invoiceRepository.findOne(invoiceId);
        ExceptionUtils.throwIfNull(one);

        // 権限のないチケットなら例外にする
        Ticket ticket = one.getTicket();
        ExceptionUtils.throwIfNotEqual(clinicId, ticket.getClinic().getId());

        // インヴォイスをキャンセルしてチケットのステータスを戻す
        ticket.setState(TicketState.DOING);
        ticketRepository.save(ticket);
        one.setRemoved(Boolean.TRUE);
        invoiceRepository.save(one);
    }
}
