package org.majimena.petz.repository;

import org.majimena.petz.domain.Examination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * 診察リポジトリ.
 */
public interface ExaminationRepository
        extends JpaRepository<Examination, String>, JpaSpecificationExecutor<Examination> {

    /**
     * チケットIDをもとに診察情報を取得する.
     *
     * @param ticketId チケットID
     * @return 診察情報の一覧
     */
    List<Examination> findByTicketId(String ticketId);
}
