package org.majimena.petz.repository;

import org.majimena.petz.domain.Chart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

/**
 * カルテリポジトリ.
 */
public interface ChartRepository extends JpaRepository<Chart, String>, JpaSpecificationExecutor<Chart> {

    Optional<Chart> findByClinicIdAndCustomerIdAndPetId(String clinicId, String customerId, String petId);

}
