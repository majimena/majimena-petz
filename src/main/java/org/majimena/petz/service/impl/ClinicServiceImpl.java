package org.majimena.petz.service.impl;

import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.User;
import org.majimena.petz.domain.clinic.ClinicCriteria;
import org.majimena.petz.repository.ClinicRepository;
import org.majimena.petz.repository.UserRepository;
import org.majimena.petz.security.SecurityUtils;
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

    @Override
    public Optional<Clinic> getClinicById(Long clinicId) {
        Optional<Clinic> one = Optional.ofNullable(clinicRepository.findOne(clinicId));
        return one;
    }

    @Override
    public Page<Clinic> getClinics(ClinicCriteria criteria, Pageable pageable) {
        return clinicRepository.findAll(pageable);
    }

    @Override
    public Optional<Clinic> saveClinic(Clinic clinic) {
        String login = SecurityUtils.getCurrentLogin();
        Optional<User> owner = userRepository.findOneByLogin(login);
        owner.ifPresent(u -> {
            clinic.setOwnerUser(u);
            clinicRepository.save(clinic);
        });
        return Optional.of(clinic);
    }

    @Override
    public Optional<Clinic> updateClinic(Clinic clinic) {
        Optional<Clinic> one = getClinicById(clinic.getId());
        one.ifPresent(p -> {
            p.setName(clinic.getName());
            p.setDescription(clinic.getDescription());
            clinicRepository.save(p);
        });
        return one;
    }

    @Override
    public void deleteClinic(Long clinicId) {
        clinicRepository.delete(clinicId);
    }
}
