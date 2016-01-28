package org.majimena.petz.repository;

import org.majimena.petz.domain.Chart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

/**
 * カルテリポジトリ.
 */
public interface ChartRepository extends JpaRepository<Chart, String>, JpaSpecificationExecutor<Chart> {

    /**
     * 顧客IDをもとに、該当するカルテを全て取得する.
     *
     * @param customerId 顧客ID
     * @return カルテの一覧
     */
    List<Chart> findByCustomerId(String customerId);

    Optional<Chart> findByClinicIdAndCustomerIdAndPetId(String clinicId, String customerId, String petId);
}
