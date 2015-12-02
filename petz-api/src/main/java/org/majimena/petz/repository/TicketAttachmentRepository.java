package org.majimena.petz.repository;

import org.majimena.petz.domain.TicketAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * チケット添付ファイルのリポジトリ.
 */
public interface TicketAttachmentRepository
        extends JpaRepository<TicketAttachment, String>, JpaSpecificationExecutor<TicketAttachment> {

    /**
     * チケットIDをもとに、チケット添付ファイルを取得する.
     *
     * @param ticketId チケットID
     * @return 該当するチケット添付ファイル
     */
    List<TicketAttachment> findByTicketId(String ticketId);
}
