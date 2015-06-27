package org.majimena.petz.service;

import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.clinic.ClinicCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * クリニックサービス.
 */
public interface ClinicService {

    Optional<Clinic> getClinicById(Long clinicId);

    Page<Clinic> getClinics(ClinicCriteria criteria, Pageable pageable);

    Optional<Clinic> saveClinic(Clinic clinic);

    Optional<Clinic> updateClinic(Clinic clinic);

    void deleteClinic(Long clinicId);

}
