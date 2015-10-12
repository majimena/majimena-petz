package org.majimena.petz.web.api.examination;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.Customer;
import org.majimena.petz.domain.Pet;
import org.majimena.petz.domain.Schedule;
import org.majimena.petz.domain.User;
import org.majimena.petz.domain.errors.ErrorCode;
import org.majimena.petz.repository.ClinicRepository;
import org.majimena.petz.repository.CustomerRepository;
import org.majimena.petz.repository.PetRepository;
import org.majimena.petz.repository.ScheduleRepository;
import org.majimena.petz.repository.UserRepository;
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
            validateRequired(schedule, errors);
            validateClinicId(schedule, errors);
            validateUserId(schedule, errors);
            validatePetId(schedule, errors);
            validateCustomerId(schedule, errors);
            validateScheduleId(schedule, errors);
        });
    }

    private void validateRequired(Schedule schedule, Errors errors) {
        ErrorsUtils.rejectIfAllNull(new Object[]{schedule.getUser(), schedule.getCustomer()}, errors);
    }

    private void validateClinicId(Schedule schedule, Errors errors) {
        if (schedule.getClinic() != null) {
            String id = schedule.getClinic().getId();
            Clinic one = clinicRepository.findOne(id);
            if (one == null) {
                ErrorsUtils.rejectValue("clinic", ErrorCode.PTZ_001999, errors);
            }
        }
    }

    private void validateUserId(Schedule schedule, Errors errors) {
        if (schedule.getUser() != null) {
            String id = schedule.getUser().getId();
            User one = userRepository.findOne(id);
            if (one == null) {
                ErrorsUtils.rejectValue("user", ErrorCode.PTZ_000999, errors);
            }
        }
    }

    private void validatePetId(Schedule schedule, Errors errors) {
        if (schedule.getPet() != null) {
            String id = schedule.getPet().getId();
            Pet one = petRepository.findOne(id);
            if (one == null) {
                ErrorsUtils.rejectValue("pet", ErrorCode.PTZ_002999, errors);
            }
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
