package org.majimena.petical.repository;

import org.majimena.petical.domain.ClinicInspection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 動物病院診察料金リポジトリ.
 */
public interface ClinicInspectionRepository extends JpaRepository<ClinicInspection, String>, JpaSpecificationExecutor<ClinicInspection> {

    @Query(value = "insert into clinic_inspection (id, clinic_id, course, category, name, price, insurance, unit, tax_type, tax_rate, description, created_by, created_date, last_modified_by, last_modified_date) "
            + "select uuid(), :clinicId, course, category, name, price, insurance, unit, tax_type, tax_rate, description, :userId, now(), :userId, now() "
            + "from inspection order by id", nativeQuery = true)
    @Modifying
    int setup(@Param("clinicId") String clinicId, @Param("userId") String userId);

    List<ClinicInspection> findByClinicId(String clinicId);

}
