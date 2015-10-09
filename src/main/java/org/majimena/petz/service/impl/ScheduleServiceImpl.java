package org.majimena.petz.service.impl;

import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.Customer;
import org.majimena.petz.domain.Pet;
import org.majimena.petz.domain.Schedule;
import org.majimena.petz.domain.User;
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
    @Transactional
    public Schedule saveSchedule(Schedule schedule) {
        // 必須の関連マスタを取得する（事前にチェックしているので、通常データがないことはありえない）
        Clinic clinic = clinicRepository.findOne(schedule.getClinic().getId());
        User user = userRepository.findOne(schedule.getUser().getId());
        Pet pet = petRepository.findOne(schedule.getPet().getId());

        // 顧客が特定できている場合はそのまま使い、顧客が特定できていない場合は既に顧客登録されていないか確認する
        Customer customer;
        if (schedule.getCustomer() != null) {
            customer = customerRepository.findOne(schedule.getCustomer().getId());
        } else {
            customer = customerRepository.findByClinicIdAndUserId(clinic.getId(), user.getId()).orElse(null);
        }

        // スケジュールと関連マスタを関連付けて保存する
        schedule.setClinic(clinic);
        schedule.setUser(user);
        schedule.setPet(pet);
        schedule.setCustomer(customer);
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
        Customer customer;
        if (schedule.getCustomer() != null) {
            customer = customerRepository.findOne(schedule.getCustomer().getId());
        } else {
            customer = customerRepository.findByClinicIdAndUserId(clinic.getId(), user.getId()).orElse(null);
        }

        // スケジュールと関連マスタを関連付けて保存する
        persist.setClinic(clinic);
        persist.setUser(user);
        persist.setPet(pet);
        persist.setCustomer(customer);
        return scheduleRepository.save(schedule);
    }
}
