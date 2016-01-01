package org.majimena.petz.service.impl;

import org.majimena.petz.common.utils.ExceptionUtils;
import org.majimena.petz.datatype.InvoiceState;
import org.majimena.petz.datatype.TicketState;
import org.majimena.petz.datetime.L10nDateTimeProvider;
import org.majimena.petz.domain.Invoice;
import org.majimena.petz.domain.Payment;
import org.majimena.petz.domain.Ticket;
import org.majimena.petz.repository.InvoiceRepository;
import org.majimena.petz.repository.PaymentRepository;
import org.majimena.petz.repository.TicketRepository;
import org.majimena.petz.service.PaymentService;
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
            invoice.setState(InvoiceState.PAID);
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
