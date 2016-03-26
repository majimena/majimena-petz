package org.majimena.petical.service.impl;

import org.majimena.petical.common.utils.ExceptionUtils;
import org.majimena.petical.datatype.TicketState;
import org.majimena.petical.datetime.L10nDateTimeProvider;
import org.majimena.petical.domain.Invoice;
import org.majimena.petical.domain.Payment;
import org.majimena.petical.domain.Ticket;
import org.majimena.petical.repository.InvoiceRepository;
import org.majimena.petical.repository.PaymentRepository;
import org.majimena.petical.repository.TicketRepository;
import org.majimena.petical.service.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ペイメントサービスの実装.
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    /**
     * ペイメントリポジトリ.
     */
    @Inject
    private PaymentRepository paymentRepository;

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
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Payment savePayment(Payment payment) {
        // 請求情報を取得する
        Invoice invoice = payment.getInvoice();
        ExceptionUtils.throwIfNull(invoice);

        BigDecimal amount = invoice.getReceiptAmount().add(payment.getAmount());
        if (invoice.getTotal().compareTo(amount) <= 0) {
            // 全額支払を済ませた場合
            invoice.setPaid(Boolean.TRUE);
            invoice.setReceiptAmount(amount);
            invoice.setReceiptDateTime(LocalDateTime.from(L10nDateTimeProvider.now()));
            invoiceRepository.save(invoice);

            // 会計済みなのでチケットも完了させる
            Ticket ticket = invoice.getTicket();
            ticket.setState(TicketState.COMPLETED);
            ticketRepository.save(ticket);
        } else {
            // 全額支払ができなかった場合
            invoice.setReceiptAmount(amount);
            invoiceRepository.save(invoice);
        }

        // 支払情報を保存
        Payment save = paymentRepository.save(payment);
        return save;
    }
}
