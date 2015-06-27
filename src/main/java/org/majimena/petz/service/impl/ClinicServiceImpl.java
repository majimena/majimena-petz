package org.majimena.petz.service.impl;

import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.clinic.ClinicCriteria;
import org.majimena.petz.repository.ClinicRepository;
import org.majimena.petz.repository.UserRepository;
import org.majimena.petz.service.ClinicService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Optional;

/**
 * クリニックサービスの実装クラス.
 */
@Service
@Transactional
public class ClinicServiceImpl implements ClinicService {

    @Inject
    private ClinicRepository clinicRepository;

    @Inject
    private UserRepository userRepository;

    public void setClinicRepository(ClinicRepository clinicRepository) {
        this.clinicRepository = clinicRepository;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Clinic> getClinicById(Long clinicId) {
        return Optional.ofNullable(clinicRepository.findOne(clinicId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Clinic> getClinics(ClinicCriteria criteria, Pageable pageable) {
        return clinicRepository.findAll(pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Clinic> saveClinic(Clinic clinic) {
        Clinic save = clinicRepository.save(clinic);
        return Optional.ofNullable(save);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Clinic> updateClinic(Clinic clinic) {
        Optional<Clinic> one = getClinicById(clinic.getId());
        one.ifPresent(c -> {
            c.setName(clinic.getName());
            c.setDescription(clinic.getDescription());
            clinicRepository.save(c);
        });
        return one;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteClinic(Long clinicId) {
        clinicRepository.delete(clinicId);
    }

}
