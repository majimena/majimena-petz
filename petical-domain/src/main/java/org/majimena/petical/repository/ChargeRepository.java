package org.majimena.petical.repository;

import org.majimena.petical.domain.Charge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 診察料金リポジトリ.
 */
public interface ChargeRepository extends JpaRepository<Charge, String>, JpaSpecificationExecutor<Charge> {

    @Query(value = "insert into clinic_charge "
            + "select uuid(), :clinicId, course, category, name, price, insurance, unit, tax_type, tax_rate, description, false, :userId, now(), :userId, now() "
            + "from charge order by id", nativeQuery = true)
    void setup(@Param("clinicId") String clinicId, @Param("userId") String userId);

}
