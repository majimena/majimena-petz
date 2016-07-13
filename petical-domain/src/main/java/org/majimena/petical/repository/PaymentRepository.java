package org.majimena.petical.repository;

import org.majimena.petical.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * ペイメントリポジトリ.
 */
public interface PaymentRepository
        extends JpaRepository<Payment, String>, JpaSpecificationExecutor<Payment> {

}
