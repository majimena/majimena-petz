package org.majimena.petz.web.api.vaccine;

import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.Vaccine;
import org.majimena.petz.domain.errors.ErrorCode;
import org.majimena.petz.repository.ClinicRepository;
import org.majimena.petz.web.api.AbstractValidator;
import org.majimena.petz.web.utils.ErrorsUtils;
import org.springframework.validation.Errors;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Optional;

/**
 * ワクチンドメインのカスタムバリデータ.
 */
@Named("vaccineValidator")
public class VaccineValidator extends AbstractValidator<Vaccine> {

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
            Clinic clinic = validateClinicId(vaccine.getClinic().getId(), errors);
            vaccine.setClinic(clinic);
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
