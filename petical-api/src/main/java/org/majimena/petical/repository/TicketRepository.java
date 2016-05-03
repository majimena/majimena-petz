package org.majimena.petical.repository;

import org.majimena.petical.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * チケットリポジトリ.
 */
public interface TicketRepository
        extends JpaRepository<Ticket, String>, JpaSpecificationExecutor<Ticket> {

}
