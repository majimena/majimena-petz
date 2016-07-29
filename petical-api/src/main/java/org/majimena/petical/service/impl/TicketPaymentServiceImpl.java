package org.majimena.petical.service.impl;

import org.majimena.petical.common.utils.ExceptionUtils;
import org.majimena.petical.datatype.TicketState;
import org.majimena.petical.domain.Ticket;
import org.majimena.petical.domain.TicketPayment;
import org.majimena.petical.repository.TicketPaymentRepository;
import org.majimena.petical.repository.TicketRepository;
import org.majimena.petical.service.TicketPaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

/**
 * チケット支払サービスの実装.
 */
@Service
public class TicketPaymentServiceImpl implements TicketPaymentService {

    @Inject
    private TicketRepository ticketRepository;

    /**
     * チケット支払リポジトリ.
     */
    @Inject
    private TicketPaymentRepository ticketPaymentRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<TicketPayment> getTicketPaymentsByTicketId(String ticketId) {
        List<TicketPayment> payments = ticketPaymentRepository.findByTicketId(ticketId);
        return payments;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public TicketPayment saveTicketPayment(TicketPayment payment) {
        // チケットを取得
        Ticket ticket = ticketRepository.findOne(payment.getTicket().getId());
        ExceptionUtils.throwIfNull(ticket);

        // 不足金額を今回の請求額とする
        payment.setTotal(ticket.getBalance());

        // おつり＝請求額ー割引額ー受領額の絶対値とする
        BigDecimal changed = payment.getTotal().subtract(payment.getDiscount()).subtract(payment.getAmount());
        if (changed.compareTo(BigDecimal.ZERO) > 0) {
            payment.setChanged(BigDecimal.ZERO);

            // 支払が不足しているので不足金額として記録
            ticket.setBalance(changed);
            ticket.setDiscount(ticket.getDiscount().add(payment.getDiscount()));
            ticket.setState(TicketState.PAYMENT);
        } else {
            payment.setChanged(changed.abs());

            // おつりが発生している場合は支払が完了しているのでチケットをクローズする
            ticket.setBalance(BigDecimal.ZERO);
            ticket.setDiscount(ticket.getDiscount().add(payment.getDiscount()));
            ticket.setState(TicketState.COMPLETED);
        }

        // 支払情報を保存
        TicketPayment saved = ticketPaymentRepository.save(payment);
        // チケットを保存
        ticketRepository.save(ticket);

        return saved;
    }
}
