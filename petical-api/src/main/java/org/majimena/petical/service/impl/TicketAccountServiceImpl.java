package org.majimena.petical.service.impl;

import org.majimena.petical.common.exceptions.ApplicationException;
import org.majimena.petical.datatype.TicketState;
import org.majimena.petical.domain.Ticket;
import org.majimena.petical.domain.TicketAccount;
import org.majimena.petical.domain.TicketInspection;
import org.majimena.petical.domain.TicketPayment;
import org.majimena.petical.domain.errors.ErrorCode;
import org.majimena.petical.repository.TicketAccountRepository;
import org.majimena.petical.repository.TicketInspectionRepository;
import org.majimena.petical.repository.TicketPaymentRepository;
import org.majimena.petical.repository.TicketRepository;
import org.majimena.petical.service.TicketAccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * チケット会計サービスの実装.
 */
@Service
public class TicketAccountServiceImpl implements TicketAccountService {

    /**
     * チケットリポジトリ.
     */
    @Inject
    private TicketRepository ticketRepository;

    /**
     * チケット会計リポジトリ.
     */
    @Inject
    private TicketAccountRepository ticketAccountRepository;

    /**
     * チケット検査リポジトリ.
     */
    @Inject
    private TicketInspectionRepository ticketInspectionRepository;

    /**
     * チケット支払リポジトリ.
     */
    @Inject
    private TicketPaymentRepository ticketPaymentRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public List<TicketAccount> getTicketAccountsByTicketId(String ticketId, boolean force) {
        List<TicketAccount> accounts = ticketAccountRepository.findByTicketId(ticketId);
        if (accounts.isEmpty() || force) {
            // 再作成の場合は会計情報を削除する
            if (force) {
                ticketAccountRepository.removeByTicketId(ticketId);
                accounts = new ArrayList<>();
            }

            // 既に会計情報が作成されている場合は変更できない
            List<TicketPayment> payments = ticketPaymentRepository.findByTicketId(ticketId);
            if (!payments.isEmpty()) {
                throw new ApplicationException(ErrorCode.PTZ_100201);
            }

            // 検査内容から会計情報を作成する
            Ticket ticket = ticketRepository.findOne(ticketId);
            List<TicketInspection> inspections = ticketInspectionRepository.findByTicketIdOrderByCreatedDateAsc(ticketId);
            List<TicketAccount> accounts1 = inspections.stream()
                    .map(inspection -> from(ticket, inspection))
                    .map(account -> ticketAccountRepository.save(account))
                    .collect(Collectors.toList());
            accounts.addAll(accounts1);

            // TODO 処方内容から会計情報を作成する

            // チケットの合計金額を計算する
            BigDecimal tax = BigDecimal.ZERO;
            BigDecimal total = BigDecimal.ZERO;
            for (TicketAccount account : accounts) {
                tax = tax.add(account.getTax());
                total = total.add(account.getSubtotal());
            }

            // ステートを変えてチケットを保存する
            ticket.setState(TicketState.PAYMENT);
            ticket.setTax(tax);
            ticket.setTotal(total);
            ticket.setBalance(total);
            ticket.setDiscount(BigDecimal.ZERO);
            ticketRepository.save(ticket);
        }
        return accounts;
    }

    protected TicketAccount from(Ticket ticket, TicketInspection inspection) {
        return TicketAccount.builder()
                .ticket(ticket)
                .name(inspection.getName())
                .price(inspection.getPrice())
                .quantity(inspection.getQuantity())
                .amount(inspection.getAmount())
                .tax(inspection.getTax())
                .subtotal(inspection.getSubtotal()).build();
    }
}
