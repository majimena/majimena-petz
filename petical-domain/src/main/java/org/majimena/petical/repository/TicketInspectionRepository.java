package org.majimena.petical.repository;

import org.majimena.petical.domain.TicketInspection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * チケット検査トランのリポジトリ.
 */
public interface TicketInspectionRepository extends JpaRepository<TicketInspection, String>, JpaSpecificationExecutor<TicketInspection> {

    List<TicketInspection> findByTicketIdOrderByCreatedDateAsc(String ticketId);

}
