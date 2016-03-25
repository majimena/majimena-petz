package org.majimena.petz.web.api.clinic;

import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.errors.ErrorCode;
import org.majimena.petz.repository.ClinicRepository;
import org.majimena.petz.web.api.AbstractValidator;
import org.majimena.petz.web.utils.ErrorsUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Optional;

/**
 * クリニックドメインのカスタムバリデータ.
 */
@Named("clinicValidator")
@Transactional(readOnly = true)
public class ClinicValidator extends AbstractValidator<Clinic> {

    /**
     * クリニックリポジトリ.
     */
    @Inject
    private ClinicRepository clinicRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validate(Optional<Clinic> target, Errors errors) {
        target.ifPresent(clinic -> {
            // ID存在チェック
            validateId(Optional.ofNullable(clinic.getId()), errors);
        });
    }

    private void validateId(Optional<String> value, Errors errors) {
        value.ifPresent(id -> {
            Clinic one = clinicRepository.findOne(id);
            if (one == null) {
                ErrorsUtils.rejectValue("id", ErrorCode.PTZ_999998, errors);
            }
        });
    }
}
