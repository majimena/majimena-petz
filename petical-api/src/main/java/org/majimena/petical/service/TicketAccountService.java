package org.majimena.petical.service;

import org.majimena.petical.domain.TicketAccount;

import java.util.List;

/**
 * チケット会計サービス.
 */
public interface TicketAccountService {

    /**
     * チケットIDをもとに、会計情報を取得する. 会計情報がまだ存在しない場合は現在のチケットをもとに新しく作成する.
     *
     * @param ticketId チケットID
     * @param force    会計情報を再作成する場合はTRUE
     * @return チケットの会計情報
     */
    List<TicketAccount> getTicketAccountsByTicketId(String ticketId, boolean force);

}
