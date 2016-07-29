package org.majimena.petical.repository;

import org.majimena.petical.domain.TicketPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * チケット支払リポジトリ.
 */
public interface TicketPaymentRepository extends JpaRepository<TicketPayment, String>, JpaSpecificationExecutor<TicketPayment> {

    List<TicketPayment> findByTicketId(String ticketId);
}
