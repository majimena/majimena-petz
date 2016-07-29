package org.majimena.petical.service.impl;

import org.majimena.petical.domain.Medicine;
import org.majimena.petical.repository.ClinicMedicineRepository;
import org.majimena.petical.service.ClinicMedicineService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

/**
 * 動物病院医薬品サービスの実装.
 */
@Service
@Transactional
public class ClinicMedicineServiceImpl implements ClinicMedicineService {
    /**
     * 動物病院医薬品リポジトリ.
     */
    @Inject
    private ClinicMedicineRepository clinicMedicineRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Medicine> getMedicinesByClinicId(String clinicId) {
        return clinicMedicineRepository.findMedicinesByClinicId(clinicId);
    }
}
