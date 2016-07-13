package org.majimena.petical.repository;

import org.majimena.petical.domain.TicketInspection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * チケット検査トランのリポジトリ.
 */
public interface TicketInspectionRepository extends JpaRepository<TicketInspection, String>, JpaSpecificationExecutor<TicketInspection> {

    /**
     * チケットをもとにチケットの検査情報を取得する.
     *
     * @param ticketId チケットID
     * @return チケットが持つ検査情報
     */
    List<TicketInspection> findByTicketIdOrderByCreatedDateAsc(String ticketId);

}
