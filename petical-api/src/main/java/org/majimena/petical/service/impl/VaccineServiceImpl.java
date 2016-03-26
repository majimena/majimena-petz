package org.majimena.petical.service.impl;

import org.majimena.petical.common.utils.BeanFactoryUtils;
import org.majimena.petical.common.utils.ExceptionUtils;
import org.majimena.petical.domain.Vaccine;
import org.majimena.petical.domain.vaccine.VaccineCriteria;
import org.majimena.petical.repository.VaccineRepository;
import org.majimena.petical.repository.spec.VaccineSpecs;
import org.majimena.petical.service.VaccineService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

/**
 * プロダクトサービスの実装.
 */
@Service
public class VaccineServiceImpl implements VaccineService {

    /**
     * ワクチンリポジトリ.
     */
    @Inject
    private VaccineRepository vaccineRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Vaccine> getVaccinesByVaccineCriteria(VaccineCriteria criteria) {
        List<Vaccine> vaccines = vaccineRepository.findAll(VaccineSpecs.of(criteria));
        return vaccines;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Vaccine> getVaccineByVaccineId(String vaccineId) {
        Vaccine vaccine = vaccineRepository.findOne(vaccineId);
        return Optional.ofNullable(vaccine);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Vaccine saveVaccine(Vaccine vaccine) {
        vaccine.setRemoved(Boolean.FALSE);
        Vaccine save = vaccineRepository.save(vaccine);
        return save;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Vaccine updateVaccine(Vaccine vaccine) {
        // 万が一、更新対象がなくなっていたらエラー
        Vaccine one = vaccineRepository.findOne(vaccine.getId());
        ExceptionUtils.throwIfNull(one);

        // 値があるデータを上書きコピーして保存
        BeanFactoryUtils.copyNonNullProperties(vaccine, one);
        one.setRemoved(Boolean.FALSE);
        return vaccineRepository.save(one);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteVaccine(Vaccine vaccine) {
        Vaccine one = vaccineRepository.findOne(vaccine.getId());
        one.setRemoved(Boolean.TRUE);
        vaccineRepository.delete(one);
    }
}
