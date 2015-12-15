package org.majimena.petz.service.impl;

import org.majimena.petz.common.exceptions.ResourceConflictException;
import org.majimena.petz.common.exceptions.ResourceNotFoundException;
import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.ClinicStaff;
import org.majimena.petz.domain.User;
import org.majimena.petz.domain.clinic.ClinicCriteria;
import org.majimena.petz.repository.ClinicRepository;
import org.majimena.petz.repository.ClinicStaffRepository;
import org.majimena.petz.repository.UserRepository;
import org.majimena.petz.repository.spec.ClinicSpecs;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.ClinicService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;
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
    private ClinicStaffRepository clinicStaffRepository;

    @Inject
    private UserRepository userRepository;

    public void setClinicRepository(ClinicRepository clinicRepository) {
        this.clinicRepository = clinicRepository;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setClinicStaffRepository(ClinicStaffRepository clinicStaffRepository) {
        this.clinicStaffRepository = clinicStaffRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Clinic> getClinicById(String clinicId) {
        return Optional.ofNullable(clinicRepository.findOne(clinicId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Clinic> findClinicsByClinicCriteria(ClinicCriteria criteria, Pageable pageable) {
        return clinicRepository.findAll(ClinicSpecs.of(criteria), pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Clinic> findMyClinicsByClinicCriteria(ClinicCriteria criteria, Pageable pageable) {
        // TODO マイクリニックの機能を実装する予定（お気に入り＋既に診察しているクリニック）
        return getClinics(criteria, pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Clinic> getClinics(ClinicCriteria criteria, Pageable pageable) {
        return clinicStaffRepository.findClinicsByUserId(criteria.getUserId(), pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ClinicStaff> getClinicStaffsById(String clinicId) {
        // 自分が所属するクリニックのスタッフだけが取得できるようにチェックする
        String currentUserId = SecurityUtils.getCurrentUserId();
        Optional<ClinicStaff> staff = clinicStaffRepository.findByClinicIdAndUserId(clinicId, currentUserId);
        staff.orElseThrow(() -> new ResourceNotFoundException("cannot read ClinicStaff for clinicId=[" + clinicId + "]"));

        List<ClinicStaff> clinics = clinicStaffRepository.findByClinicId(clinicId);
        clinics.stream().forEach(cs -> cs.getUser().getAuthorities().size()); // lazy loading authorities
        return clinics;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Clinic saveClinic(Clinic clinic) {
        // クリニックを登録
        Clinic save = clinicRepository.save(clinic);

        // クリニックのオーナーとして自分を登録
        String userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findOne(userId);
        ClinicStaff staff = new ClinicStaff(null, save, user, "ROLE_OWNER", LocalDate.now());
        clinicStaffRepository.save(staff);

        return save;
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
            c.setEmail(clinic.getEmail());
            clinicRepository.save(c);
        });
        return one;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteClinic(String clinicId) {
        clinicStaffRepository.deleteAll();
        clinicRepository.delete(clinicId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteClinicStaff(String clinicId, String userId) {
        // 自分が所属するクリニックのスタッフだけが取得できるようにチェックする
        String currentUserId = SecurityUtils.getCurrentUserId();
        Optional<ClinicStaff> staff = clinicStaffRepository.findByClinicIdAndUserId(clinicId, currentUserId);
        staff.orElseThrow(() -> new ResourceNotFoundException("cannot read ClinicStaff for clinicId=[" + clinicId + "]")); // FIXME Exception type

        // 該当ユーザーのクリニック紐付けを削除する
        Optional<ClinicStaff> target = clinicStaffRepository.findByClinicIdAndUserId(clinicId, userId);
        target.ifPresent(s -> clinicStaffRepository.delete(s));
        target.orElseThrow(() -> new ResourceConflictException("conflict ClinicStaff resource for clinicId=[" + clinicId + "] and userId=[" + userId + "]"));
    }

}
