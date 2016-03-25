package org.majimena.petz.service;

import org.majimena.petz.domain.Ticket;
import org.majimena.petz.domain.graph.Graph;
import org.majimena.petz.domain.ticket.ClinicChartTicketCriteria;
import org.majimena.petz.domain.ticket.TicketCriteria;

import java.util.List;
import java.util.Optional;

/**
 * チケットサービス.
 */
public interface TicketService {

    /**
     * チケットクライテリアをもとに、チケットを検索する.
     *
     * @param criteria チケットクライテリア
     * @return 該当するチケットの一覧
     */
    List<Ticket> getTicketsByTicketCriteria(TicketCriteria criteria);

    /**
     * クリニックカルテチケットクライテリアをもとに、チケットを検索する.
     *
     * @param criteria クリニックカルテチケットクライテリア
     * @return 該当するチケットの一覧
     */
    List<Ticket> getTicketsByClinicChartTicketCriteria(ClinicChartTicketCriteria criteria);

    /**
     * チケットIDをもとに、チケットを取得する.
     *
     * @param ticketId チケットID
     * @return 該当するチケット
     */
    Optional<Ticket> getTicketByTicketId(String ticketId);

    /**
     * 本日分のチケットのグラフデータを取得する.
     *
     * @param clinicId クリニックID
     * @return 本日分のチケットデータ
     */
    Graph getTodaysTicketGraphByClinicId(String clinicId);

    /**
     * チケットを新規作成する.
     *
     * @param ticket チケット
     * @return 登録したチケット
     */
    Ticket saveTicket(Ticket ticket);

    /**
     * チケットを更新する.
     *
     * @param ticket チケット
     * @return 更新したチケット
     */
    Ticket updateTicket(Ticket ticket);

    /**
     * チケットのIDをもとに、チケットを削除する.
     *
     * @param ticketId チケットのID
     */
    void deleteTicketByTicketId(String ticketId);

    /**
     * チケットのステータスを次に進める.
     *
     * @param ticketId チケットのID
     * @return 更新したチケット
     */
    Ticket signalTicketStatus(String ticketId);
}
