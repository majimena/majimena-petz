package org.majimena.petical.repository;

import org.majimena.petical.domain.ClinicInspection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 動物病院診察料金リポジトリ.
 */
public interface ClinicInspectionRepository extends JpaRepository<ClinicInspection, String>, JpaSpecificationExecutor<ClinicInspection> {

    @Query(value = "insert into clinic_inspection "
            + "select uuid(), :clinicId, course, category, name, price, insurance, unit, tax_type, tax_rate, description, false, :userId, now(), :userId, now() "
            + "from inspection order by id", nativeQuery = true)
    void setup(@Param("clinicId") String clinicId, @Param("userId") String userId);

    List<ClinicInspection> findByClinicId(String clinicId);

}
