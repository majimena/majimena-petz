package org.majimena.petz.service;

import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.ClinicStaff;
import org.majimena.petz.domain.clinic.ClinicCriteria;
import org.majimena.petz.domain.clinic.ClinicOutline;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * クリニックサービス.
 */
public interface ClinicService {

    Optional<Clinic> getClinicById(String clinicId);

    Optional<ClinicOutline> getClinicOutlineByClinicId(String clinicId);

    Page<Clinic> findClinicsByClinicCriteria(ClinicCriteria criteria, Pageable pageable);

    Page<Clinic> findMyClinicsByClinicCriteria(ClinicCriteria criteria, Pageable pageable);

    Page<Clinic> getClinics(ClinicCriteria criteria, Pageable pageable);

    List<ClinicStaff> getClinicStaffsById(String clinicId);

    Clinic saveClinic(Clinic clinic);

    Optional<Clinic> updateClinic(Clinic clinic);

    void deleteClinic(String clinicId);

    void deleteClinicStaff(String clinicId, String userId);
}
