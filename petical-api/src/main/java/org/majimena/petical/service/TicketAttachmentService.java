package org.majimena.petical.service;

import org.majimena.petical.domain.TicketAttachment;
import org.majimena.petical.domain.ticket.TicketAttachmentCriteria;

import java.util.List;
import java.util.Optional;

/**
 * チケット添付ファイルサービス.
 */
public interface TicketAttachmentService {

    /**
     * チケット添付ファイルクライテリアをもとに、チケット添付ファイルを検索する.
     *
     * @param criteria チケット添付ファイルクライテリア
     * @return 該当するチケット添付ファイルの一覧
     */
    List<TicketAttachment> getTicketAttachmentsByTicketAttachmentCriteria(TicketAttachmentCriteria criteria);

    /**
     * チケット添付ファイルIDをもとに、チケット添付ファイルを取得する.
     *
     * @param attachmentId チケット添付ファイルID
     * @return 該当するチケット添付ファイル
     */
    Optional<TicketAttachment> getTicketAttachmentByTicketAttachmentId(String attachmentId);

    /**
     * チケット添付ファイルを新規作成する.
     *
     * @param clinicId クリニックID
     * @param ticketId チケットID
     * @param name     ファイル名
     * @param binary   添付するバイナリデータ
     * @return 登録したチケット添付ファイル
     */
    TicketAttachment saveTicketAttachment(String clinicId, String ticketId, String name, byte[] binary);

    /**
     * チケット添付ファイルを更新する.
     *
     * @param attachment チケット添付ファイル
     * @return 更新したチケット添付ファイル
     */
    TicketAttachment updateTicketAttachment(TicketAttachment attachment);

    /**
     * チケット添付ファイルのIDをもとに、チケット添付ファイルを削除する.
     *
     * @param clinicId     クリニックID
     * @param ticketId     チケットID
     * @param attachmentId チケット添付ファイルID
     */
    void deleteTicketAttachmentByTicketAttachmentId(String clinicId, String ticketId, String attachmentId);
}
