package org.majimena.petical.repository;

import org.majimena.petical.domain.TicketAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * チケット会計リポジトリ.
 */
public interface TicketAccountRepository extends JpaRepository<TicketAccount, String>, JpaSpecificationExecutor<TicketAccount> {

    List<TicketAccount> findByTicketId(String ticketId);

    void removeByTicketId(String ticketId);

}
