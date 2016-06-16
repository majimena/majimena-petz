package org.majimena.petical.web.api.charge;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petical.domain.Clinic;
import org.majimena.petical.domain.ClinicCharge;
import org.majimena.petical.domain.errors.ErrorCode;
import org.majimena.petical.repository.ClinicChargeRepository;
import org.majimena.petical.repository.ClinicRepository;
import org.majimena.petical.security.SecurityUtils;
import org.majimena.petical.web.api.AbstractValidator;
import org.majimena.petical.web.utils.ErrorsUtils;
import org.springframework.validation.Errors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * 動物病院診察料金のカスタムバリデータ.
 */
@Named("clinicChargeValidator")
public class ClinicChargeValidator extends AbstractValidator<ClinicCharge> {

    /**
     * 動物病院リポジトリ.
     */
    @Inject
    private ClinicRepository clinicRepository;

    /**
     * 動物病院診察料金リポジトリ.
     */
    @Inject
    private ClinicChargeRepository clinicChargeRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validate(ClinicCharge charge, Errors errors) {
        validateId(charge, errors);
        validateClinicId(charge, errors);
    }

    protected ClinicCharge validateId(ClinicCharge charge, Errors errors) {
        // IDがある場合は存在確認と動物病院の権限チェック
        if (StringUtils.isNotEmpty(charge.getId())) {
            ClinicCharge one = clinicChargeRepository.findOne(charge.getId());
            ErrorsUtils.rejectIfNull("id", one, errors);
            if (one != null) {
                SecurityUtils.throwIfDoNotHaveClinicRoles(one.getClinic().getId());
            }
        }
        return charge;
    }

    protected ClinicCharge validateClinicId(ClinicCharge charge, Errors errors) {
        Clinic one = clinicRepository.findOne(charge.getClinic().getId());
        if (one == null) {
            ErrorsUtils.rejectValue("clinic", ErrorCode.PTZ_001999, errors);
        } else {
            charge.setClinic(one);
        }
        return charge;
    }
}
