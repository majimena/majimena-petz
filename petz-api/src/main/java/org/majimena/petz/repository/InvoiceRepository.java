package org.majimena.petz.repository;

import org.majimena.petz.domain.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * インヴォイスリポジトリ.
 */
public interface InvoiceRepository
        extends JpaRepository<Invoice, String>, JpaSpecificationExecutor<Invoice> {

    @Query(value = "SELECT sum(total) FROM Invoice WHERE ticket.clinic.id=:clinicId AND paid=true AND receiptDateTime BETWEEN :startDateTime AND :endDateTime")
    Optional<BigDecimal> sumTotal(@Param("clinicId") String clinicId,
                                  @Param("startDateTime") LocalDateTime start, @Param("endDateTime") LocalDateTime end);
}
