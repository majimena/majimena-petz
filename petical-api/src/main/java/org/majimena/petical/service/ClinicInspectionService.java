package org.majimena.petical.service;

import org.majimena.petical.domain.ClinicInspection;

import java.util.List;
import java.util.Optional;

/**
 * 動物病院診察料金サービス.
 */
public interface ClinicInspectionService {

    List<ClinicInspection> getClinicChargesByClinicId(String clinicId);

    Optional<ClinicInspection> getClinicChargeById(String id);

    ClinicInspection saveClinicCharge(ClinicInspection charge);

    ClinicInspection updateClinicCharge(ClinicInspection charge);

    void removeClinicCharge(ClinicInspection charge);
}
