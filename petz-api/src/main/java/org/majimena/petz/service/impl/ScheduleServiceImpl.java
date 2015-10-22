package org.majimena.petz.service.impl;

import org.majimena.petz.common.exceptions.ApplicationException;
import org.majimena.petz.datatype.ScheduleStatus;
import org.majimena.petz.datetime.L10nDateTimeProvider;
import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.Customer;
import org.majimena.petz.domain.Pet;
import org.majimena.petz.domain.Schedule;
import org.majimena.petz.domain.User;
import org.majimena.petz.domain.errors.ErrorCode;
import org.majimena.petz.domain.examination.ScheduleCriteria;
import org.majimena.petz.repository.ClinicRepository;
import org.majimena.petz.repository.CustomerRepository;
import org.majimena.petz.repository.PetRepository;
import org.majimena.petz.repository.ScheduleRepository;
import org.majimena.petz.repository.UserRepository;
import org.majimena.petz.repository.spec.ScheduleCriteriaSpec;
import org.majimena.petz.service.ScheduleService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

/**
 * スケジュールサービスの実装.
 */
@Service
public class ScheduleServiceImpl implements ScheduleService {

    /**
     * スケジュールリポジトリ.
     */
    @Inject
    private ScheduleRepository scheduleRepository;

    /**
     * クリニックリポジトリ.
     */
    @Inject
    private ClinicRepository clinicRepository;

    /**
     * ユーザーリポジトリ.
     */
    @Inject
    private UserRepository userRepository;

    /**
     * ペットリポジトリ.
     */
    @Inject
    private PetRepository petRepository;

    /**
     * 顧客リポジトリ.
     */
    @Inject
    private CustomerRepository customerRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Schedule> getSchedulesByScheduleCriteria(ScheduleCriteria criteria) {
        return scheduleRepository.findAll(new ScheduleCriteriaSpec(criteria));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Schedule> getScheduleByScheduleId(String scheduleId) {
        Schedule schedule = scheduleRepository.getOne(scheduleId);
        return Optional.ofNullable(schedule);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Schedule saveSchedule(Schedule schedule) {
        // 必須の関連マスタを取得する（事前にチェックしているので、通常データがないことはありえない）
        Clinic clinic = clinicRepository.findOne(schedule.getClinic().getId());
        Pet pet = petRepository.findOne(schedule.getPet().getId());
        User user = pet.getUser();

        // 顧客が特定できている場合はそのまま使い、顧客が特定できていない場合は既に顧客登録されていないか確認する
        Customer customer = Optional.ofNullable(schedule.getCustomer())
                .map(p -> customerRepository.findOne(p.getId()))
                .orElse(customerRepository.findByClinicIdAndUserId(clinic.getId(), user.getId()).orElse(null));

        // スケジュールと関連マスタを関連付けて保存する
        schedule.setClinic(clinic);
        schedule.setPet(pet);
        schedule.setUser(user);
        schedule.setCustomer(customer);
        schedule.setStatus(ScheduleStatus.RESERVED);
        return scheduleRepository.save(schedule);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Schedule updateSchedule(Schedule schedule) {
        // 永続化されているスケジュールを取得して、そちらに値をコピーする
        Schedule persist = scheduleRepository.findOne(schedule.getId());
        BeanUtils.copyProperties(schedule, persist);

        // 必須の関連マスタを取得する（事前にチェックしているので、通常データがないことはありえない）
        Clinic clinic = clinicRepository.findOne(schedule.getClinic().getId());
        User user = userRepository.findOne(schedule.getUser().getId());
        Pet pet = petRepository.findOne(schedule.getPet().getId());

        // 顧客が特定できている場合はそのまま使い、顧客が特定できていない場合は既に顧客登録されていないか確認する
        Customer customer = Optional.ofNullable(schedule.getCustomer())
                .map(p -> customerRepository.findOne(p.getId()))
                .orElse(customerRepository.findByClinicIdAndUserId(clinic.getId(), user.getId()).orElse(null));

        // スケジュールと関連マスタを関連付けて保存する
        persist.setClinic(clinic);
        persist.setUser(user);
        persist.setPet(pet);
        persist.setCustomer(customer);
        return scheduleRepository.save(schedule);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteScheduleByScheduleId(String scheduleId) {
        Schedule schedule = scheduleRepository.findOne(scheduleId);
        if (schedule != null) {
            if (schedule.getStatus().is(ScheduleStatus.RESERVED)) {
                scheduleRepository.delete(schedule);
            } else {
                throw new ApplicationException(ErrorCode.PTZ_100101);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Schedule signalScheduleStatus(String scheduleId) {
        Schedule schedule = scheduleRepository.findOne(scheduleId);
        if (schedule == null) {
            throw new ApplicationException(ErrorCode.PTZ_100999);
        }

        ScheduleStatus next = schedule.getStatus().next();
        next.is(ScheduleStatus.RECEIPTED, s -> schedule.setReceiptDateTime(L10nDateTimeProvider.now().toLocalDateTime()));
        schedule.setStatus(next);
        return scheduleRepository.save(schedule);
    }
}
