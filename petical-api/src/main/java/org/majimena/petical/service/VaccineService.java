package org.majimena.petical.service;

import org.majimena.petical.domain.Vaccine;
import org.majimena.petical.domain.vaccine.VaccineCriteria;

import java.util.List;
import java.util.Optional;

/**
 * ワクチンサービス.
 */
public interface VaccineService {

    List<Vaccine> getVaccinesByVaccineCriteria(VaccineCriteria criteria);

    Optional<Vaccine> getVaccineByVaccineId(String vaccineId);

    Vaccine saveVaccine(Vaccine vaccine);

    Vaccine updateVaccine(Vaccine vaccine);

    void deleteVaccine(Vaccine vaccineId);
}
