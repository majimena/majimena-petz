package org.majimena.petz.web.api.examination;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petz.domain.*;
import org.majimena.petz.domain.errors.ErrorCode;
import org.majimena.petz.repository.*;
import org.majimena.petz.web.api.AbstractValidator;
import org.majimena.petz.web.utils.ErrorsUtils;
import org.springframework.validation.Errors;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Optional;

/**
 * スケジュールドメインのカスタムバリデータ.
 */
@Named("scheduleValidator")
public class ScheduleValidator extends AbstractValidator<Schedule> {

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
     * スケジュールリポジトリ.
     */
    @Inject
    private ScheduleRepository scheduleRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validate(Optional<Schedule> target, Errors errors) {
        target.ifPresent(schedule -> {
            validateClinicId(schedule, errors);
            validateUserId(schedule, errors);
            validatePetId(schedule, errors);
            validateCustomerId(schedule, errors);
            validateScheduleId(schedule, errors);
        });
    }

    private void validateClinicId(Schedule schedule, Errors errors) {
        String id = schedule.getClinic().getId();
        Clinic one = clinicRepository.findOne(id);
        if (one == null) {
            ErrorsUtils.rejectValue("clinic", ErrorCode.PTZ_001999, errors);
        }
    }

    private void validateUserId(Schedule schedule, Errors errors) {
        String id = schedule.getUser().getId();
        User one = userRepository.findOne(id);
        if (one == null) {
            ErrorsUtils.rejectValue("user", ErrorCode.PTZ_000999, errors);
        }
    }

    private void validatePetId(Schedule schedule, Errors errors) {
        String id = schedule.getPet().getId();
        Pet one = petRepository.findOne(id);
        if (one == null) {
            ErrorsUtils.rejectValue("pet", ErrorCode.PTZ_002999, errors);
        }
    }

    private void validateCustomerId(Schedule schedule, Errors errors) {
        if (schedule.getCustomer() != null) {
            String id = schedule.getCustomer().getId();
            Customer one = customerRepository.findOne(id);
            if (one == null) {
                ErrorsUtils.rejectValue("customer", ErrorCode.PTZ_003999, errors);
            }
        }
    }

    private void validateScheduleId(Schedule schedule, Errors errors) {
        if (StringUtils.isNotEmpty(schedule.getId())) {
            Schedule one = scheduleRepository.findOne(schedule.getId());
            if (one == null) {
                ErrorsUtils.reject(ErrorCode.PTZ_999998, errors);
            } else {
                if (!StringUtils.equals(one.getClinic().getId(), schedule.getClinic().getId())) {
                    ErrorsUtils.reject(ErrorCode.PTZ_999997, errors);
                }
            }
        }
    }
}
