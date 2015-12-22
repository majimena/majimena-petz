package org.majimena.petz.repository;

import org.majimena.petz.domain.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * インヴォイスリポジトリ.
 */
public interface InvoiceRepository
        extends JpaRepository<Invoice, String>, JpaSpecificationExecutor<Invoice> {

}
