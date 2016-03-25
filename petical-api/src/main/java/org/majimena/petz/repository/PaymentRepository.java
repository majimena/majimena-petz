package org.majimena.petz.repository;

import org.majimena.petz.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * ペイメントリポジトリ.
 */
public interface PaymentRepository
        extends JpaRepository<Payment, String>, JpaSpecificationExecutor<Payment> {

}
