package org.majimena.petical.service.impl;

import org.majimena.petical.common.exceptions.ResourceConflictException;
import org.majimena.petical.common.exceptions.ResourceNotFoundException;
import org.majimena.petical.common.utils.BeanFactoryUtils;
import org.majimena.petical.common.utils.DateTimeUtils;
import org.majimena.petical.common.utils.ExceptionUtils;
import org.majimena.petical.datatype.TicketState;
import org.majimena.petical.datetime.L10nDateTimeProvider;
import org.majimena.petical.domain.Clinic;
import org.majimena.petical.domain.ClinicStaff;
import org.majimena.petical.domain.User;
import org.majimena.petical.domain.chart.ChartCriteria;
import org.majimena.petical.domain.clinic.ClinicCriteria;
import org.majimena.petical.domain.clinic.ClinicOutline;
import org.majimena.petical.domain.clinic.ClinicOutlineCriteria;
import org.majimena.petical.repository.ChartRepository;
import org.majimena.petical.repository.ClinicInspectionRepository;
import org.majimena.petical.repository.ClinicRepository;
import org.majimena.petical.repository.ClinicStaffRepository;
import org.majimena.petical.repository.TicketRepository;
import org.majimena.petical.repository.UserRepository;
import org.majimena.petical.repository.spec.ChartSpecs;
import org.majimena.petical.repository.spec.ClinicSpecs;
import org.majimena.petical.repository.spec.TicketSpecs;
import org.majimena.petical.security.SecurityUtils;
import org.majimena.petical.service.ClinicService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

/**
 * クリニックサービスの実装クラス.
 */
@Service
public class ClinicServiceImpl implements ClinicService {

    /**
     * クリニックリポジトリ.
     */
    @Inject
    private ClinicRepository clinicRepository;

    /**
     * クリニックスタッフリポジトリ.
     */
    @Inject
    private ClinicStaffRepository clinicStaffRepository;

    /**
     * ユーザリポジトリ.
     */
    @Inject
    private UserRepository userRepository;

    /**
     * チケットリポジトリ.
     */
    @Inject
    private TicketRepository ticketRepository;

    /**
     * カルテリポジトリ.
     */
    @Inject
    private ChartRepository chartRepository;

    /**
     * 動物病院向け検査リポジトリ.
     */
    @Inject
    private ClinicInspectionRepository clinicInspectionRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Clinic> findClinicsByClinicCriteria(ClinicCriteria criteria, Pageable pageable) {
        Page<Clinic> page = clinicRepository.findAll(ClinicSpecs.of(criteria), pageable);
        return page;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Clinic> getMyClinicsByUserId(String userId) {
        List<Clinic> list = clinicStaffRepository.findClinicsByUserId(userId);
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Clinic> getClinicById(String clinicId) {
        Clinic one = clinicRepository.findOne(clinicId);
        return Optional.ofNullable(one);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<ClinicOutline> findClinicOutlineByClinicOutlineCriteria(ClinicOutlineCriteria criteria) {
        // 本日の予約中のチケット件数
        criteria.setState(TicketState.RESERVED);
        long reserve = ticketRepository.count(TicketSpecs.of(criteria));
        // 本日のカルテの管理件数
        long chart = chartRepository.count(ChartSpecs.of(ChartCriteria.builder().clinicId(criteria.getClinicId()).build()));
        // 本日の診察済のチケット件数
        criteria.setState(TicketState.COMPLETED);
        long examinated = ticketRepository.count(TicketSpecs.of(criteria));

        // 本日の売上金額
        LocalDateTime from = DateTimeUtils.from(criteria.getYear(), criteria.getMonth(), criteria.getDay());
        LocalDateTime to = DateTimeUtils.to(criteria.getYear(), criteria.getMonth(), criteria.getDay());

        return Optional.of(ClinicOutline.builder()
                .reserve(BigDecimal.valueOf(reserve))
                .chart(BigDecimal.valueOf(chart))
                .examinated(BigDecimal.valueOf(examinated))
                .sales(BigDecimal.ZERO)
                .build());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Clinic saveClinic(Clinic clinic) {
        ZonedDateTime now = L10nDateTimeProvider.now();

        // クリニックを登録
        clinic.setRemoved(Boolean.FALSE);
        Clinic save = clinicRepository.saveAndFlush(clinic);

        // クリニック関連マスタの初期セットアップ
        clinicInspectionRepository.setup(save.getId(), "system");

        // クリニックのオーナーとして自分を登録
        String userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findOne(userId);
        ClinicStaff staff = new ClinicStaff(null, save, user, "ROLE_OWNER", now.toLocalDateTime());
        clinicStaffRepository.save(staff);
        return save;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Clinic updateClinic(Clinic clinic) {
        // 該当エンティティを取得
        Clinic one = clinicRepository.findOne(clinic.getId());
        ExceptionUtils.throwIfNull(one);

        // コピーして保存する
        BeanFactoryUtils.copyNonNullProperties(clinic, one);
        one.setRemoved(Boolean.FALSE);
        return clinicRepository.save(one);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteClinic(String clinicId) {
        clinicStaffRepository.deleteAll();
        clinicRepository.delete(clinicId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
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
    public Optional<ClinicStaff> getClinicStaffById(String staffId) {
        ClinicStaff one = clinicStaffRepository.findOne(staffId);
        return Optional.ofNullable(one);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
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
