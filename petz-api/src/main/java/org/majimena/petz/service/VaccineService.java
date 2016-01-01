package org.majimena.petz.service;

import org.majimena.petz.domain.Vaccine;
import org.majimena.petz.domain.vaccine.VaccineCriteria;

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
