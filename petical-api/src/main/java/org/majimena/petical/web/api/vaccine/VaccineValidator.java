package org.majimena.petical.web.api.vaccine;

import org.majimena.petical.domain.Clinic;
import org.majimena.petical.domain.Vaccine;
import org.majimena.petical.domain.errors.ErrorCode;
import org.majimena.petical.repository.ClinicRepository;
import org.majimena.petical.repository.VaccineRepository;
import org.majimena.petical.web.api.AbstractValidator;
import org.majimena.petical.web.utils.ErrorsUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Optional;

/**
 * ワクチンドメインのカスタムバリデータ.
 */
@Named("vaccineValidator")
@Transactional(readOnly = true)
public class VaccineValidator extends AbstractValidator<Vaccine> {

    /**
     * ワクチンリポジトリ.
     */
    @Inject
    private VaccineRepository vaccineRepository;

    /**
     * クリニックリポジトリ.
     */
    @Inject
    private ClinicRepository clinicRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validate(Optional<Vaccine> target, Errors errors) {
        target.ifPresent(vaccine -> {
            // ID存在チェック
            validateId(Optional.ofNullable(vaccine.getId()), vaccine.getClinic().getId(), errors);

            // クリニック存在チェック
            Clinic clinic = validateClinicId(vaccine.getClinic().getId(), errors);
            vaccine.setClinic(clinic);
        });
    }

    private void validateId(Optional<String> value, String clinicId, Errors errors) {
        value.ifPresent(id -> {
            Vaccine one = vaccineRepository.findOne(id);
            if (one == null) {
                ErrorsUtils.rejectValue("id", ErrorCode.PTZ_999998, errors);
            } else {
                ErrorsUtils.throwIfNotEqual(clinicId, one.getClinic().getId());
            }
        });
    }

    private Clinic validateClinicId(String clinicId, Errors errors) {
        Clinic one = clinicRepository.findOne(clinicId);
        if (one == null) {
            ErrorsUtils.rejectValue("clinic", ErrorCode.PTZ_001999, errors);
        }
        return one;
    }
}
