package org.majimena.petical.repository;

import org.majimena.petical.domain.ClinicCharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * 動物病院診察料金リポジトリ.
 */
public interface ClinicChargeRepository extends JpaRepository<ClinicCharge, String>, JpaSpecificationExecutor<ClinicCharge> {

    List<ClinicCharge> findByClinicId(String clinicId);

}
