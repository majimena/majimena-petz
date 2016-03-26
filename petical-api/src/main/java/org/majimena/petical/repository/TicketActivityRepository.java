package org.majimena.petical.repository;

import org.majimena.petical.domain.TicketActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * チケットアクティビティリポジトリ.
 */
public interface TicketActivityRepository
        extends JpaRepository<TicketActivity, String>, JpaSpecificationExecutor<TicketActivity> {

}
