package org.majimena.petz.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petz.datatype.InvoiceState;
import org.majimena.petz.datatype.TicketState;
import org.majimena.petz.domain.Examination;
import org.majimena.petz.domain.Invoice;
import org.majimena.petz.domain.Ticket;
import org.majimena.petz.domain.ticket.InvoiceCriteria;
import org.majimena.petz.repository.ExaminationRepository;
import org.majimena.petz.repository.InvoiceRepository;
import org.majimena.petz.repository.TicketRepository;
import org.majimena.petz.repository.spec.InvoiceSpecs;
import org.majimena.petz.security.SecurityUtils;
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
    public Page<Invoice> findInvoicesByInvoiceCriteria(InvoiceCriteria criteria, Pageable pageable) {
        Page<Invoice> page = invoiceRepository.findAll(InvoiceSpecs.of(criteria), pageable);
        return page;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Invoice> getInvoiceByInvoiceId(String clinicId, String ticketId, String invoiceId) {
        Invoice one = invoiceRepository.findOne(invoiceId);
        if (one == null) {
            return Optional.empty();
        }
        if (!StringUtils.equals(ticketId, one.getTicket().getId()) || !StringUtils.equals(clinicId, one.getTicket().getClinic().getId())) {
            return Optional.empty();
        }
        return Optional.of(one);
    }

    @Override
    @Transactional
    public Invoice createInvoice(String clinicId, String ticketId) {
        Ticket ticket = ticketRepository.findOne(ticketId);
        // TODO セキュリティ対策

        // チケット自体のステータスを支払中にする
        ticket.setState(TicketState.PAYMENT);
        ticketRepository.save(ticket);

        // 請求額を計算する
        List<Examination> examinations = examinationRepository.findByTicketId(ticketId);
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal tax = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;
        examinations.forEach(examination -> {
            subtotal.add(examination.getTotal().subtract(examination.getTax()));
            tax.add(examination.getTax());
            total.add(examination.getTotal());
        });

        // インヴォイスを作成する
        Invoice invoice = new Invoice();
        invoice.setTicket(ticket);
        invoice.setState(InvoiceState.NOT_PAID);
        invoice.setSubtotal(subtotal);
        invoice.setTax(tax);
        invoice.setTotal(total);
        return invoiceRepository.save(invoice);
    }
}
