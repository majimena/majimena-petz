package org.majimena.petz.repository;

import org.majimena.petz.domain.TicketActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

/**
 * チケットアクティビティリポジトリ.
 */
public interface TicketActivityRepository
        extends JpaRepository<TicketActivity, String>, JpaSpecificationExecutor<TicketActivity> {

}
