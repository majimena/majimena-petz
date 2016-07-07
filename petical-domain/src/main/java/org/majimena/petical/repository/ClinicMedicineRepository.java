package org.majimena.petical.repository;

import org.majimena.petical.domain.ClinicMedicine;
import org.majimena.petical.domain.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 個別動物病院医薬品マスタのリポジトリ.
 */
public interface ClinicMedicineRepository extends JpaRepository<ClinicMedicine, String>, JpaSpecificationExecutor<ClinicMedicine> {

    /**
     * 個別動物病院向けの医薬品マスタの初期データをセットアップする.
     *
     * @param clinicId セットアップ対象の動物病院ID
     * @param userId   セットアップしたユーザーID
     */
    @Query(value = "insert into clinic_medicine "
            + "select uuid(), :clinicId, id, :userId, now(), :userId, now() from medicine order by id", nativeQuery = true)
    void setup(@Param("clinicId") String clinicId, @Param("userId") String userId);

    /**
     * 個別動物病院向けの医薬品マスタから使用可能な医薬品を全て取得する.
     *
     * @param clinicId 動物病院ID
     * @return 該当する医薬品
     */
    @Query(value = "select cm.medicine from ClinicMedicine cm where cm.clinic.id=:clinicId")
    List<Medicine> findMedicinesByClinicId(@Param("clinicId") String clinicId);

}
