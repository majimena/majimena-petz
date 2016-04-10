package org.majimena.petical.repository;

import org.majimena.petical.domain.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * クリニックユーザーリポジトリ.
 */
public interface CustomerRepository extends JpaRepository<Customer, String>, JpaSpecificationExecutor<Customer> {

    Optional<Customer> findByClinicIdAndUserId(String clinicId, String userId);

    Optional<Customer> findByClinicIdAndCustomerCode(String clinicId, String customerCode);

    @Deprecated
    @Query("select cu from Customer cu where cu.clinic.id = :clinicId order by cu.user.lastName, cu.user.firstName, cu.user.id")
    Page<Customer> findByClinicId(@Param("clinicId") String clinicId, Pageable pageable);
}
