package org.majimena.petical.service;

import org.majimena.petical.domain.TicketPayment;

import java.util.List;

/**
 * チケット支払サービス.
 */
public interface TicketPaymentService {

    /**
     * チケットIDをもとにチケット支払情報を取得する.
     *
     * @param ticketId チケットID
     * @return チケット支払情報
     */
    List<TicketPayment> getTicketPaymentsByTicketId(String ticketId);

    /**
     * チケット支払情報を保存する.
     *
     * @param payment チケット支払情報
     * @return 保存したチケット支払情報
     */
    TicketPayment saveTicketPayment(TicketPayment payment);

}
