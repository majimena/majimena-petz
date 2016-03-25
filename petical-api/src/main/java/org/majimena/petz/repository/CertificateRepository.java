package org.majimena.petz.repository;

import org.majimena.petz.domain.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * 証明書リポジトリ.
 */
public interface CertificateRepository
        extends JpaRepository<Certificate, String>, JpaSpecificationExecutor<Certificate> {

    /**
     * チケットIDをもとに証明書を取得する.
     *
     * @param ticketId チケットID
     * @return 該当する証明書
     */
    List<Certificate> findByTicketId(String ticketId);
}
