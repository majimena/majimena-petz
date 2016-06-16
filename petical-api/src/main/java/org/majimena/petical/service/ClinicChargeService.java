package org.majimena.petical.service;

import org.majimena.petical.domain.ClinicCharge;

import java.util.List;
import java.util.Optional;

/**
 * 動物病院診察料金サービス.
 */
public interface ClinicChargeService {

    List<ClinicCharge> getClinicChargesByClinicId(String clinicId);

    Optional<ClinicCharge> getClinicChargeById(String id);

    ClinicCharge saveClinicCharge(ClinicCharge charge);

    ClinicCharge updateClinicCharge(ClinicCharge charge);

    void removeClinicCharge(ClinicCharge charge);
}
