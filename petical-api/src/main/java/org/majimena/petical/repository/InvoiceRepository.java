package org.majimena.petical.repository;

import org.majimena.petical.domain.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

/**
 * 請求書リポジトリ.
 */
public interface InvoiceRepository
        extends JpaRepository<Invoice, String>, JpaSpecificationExecutor<Invoice> {

    /**
     * 請求書のうち、支払のあった請求書について受領金額を集計する.
     *
     * @param clinicId クリニックID
     * @param start    集計開始日時
     * @param end      集計終了日時
     * @return 受領金額の集計額（売上額）と請求書数
     */
    @Query(value = "SELECT coalesce(sum(total), 0), count(id) FROM Invoice WHERE ticket.clinic.id=:clinicId AND paid=true AND removed=false AND receiptDateTime BETWEEN :startDateTime AND :endDateTime")
    Object sumTotal(@Param("clinicId") String clinicId,
                    @Param("startDateTime") LocalDateTime start, @Param("endDateTime") LocalDateTime end);

}
