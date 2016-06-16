package org.majimena.petical.service.impl;

import org.majimena.petical.common.utils.BeanFactoryUtils;
import org.majimena.petical.common.utils.ExceptionUtils;
import org.majimena.petical.datatype.TaxType;
import org.majimena.petical.domain.ClinicCharge;
import org.majimena.petical.repository.ClinicChargeRepository;
import org.majimena.petical.service.ClinicChargeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 動物病院診察料金サービスの実装.
 */
@Service
public class ClinicChargeServiceImpl implements ClinicChargeService {

    private static final TaxType DEFAULT_TAX_TYPE = TaxType.EXCLUSIVE;

    private static final BigDecimal DEFAULT_TAX_RATE = BigDecimal.valueOf(0.08);

    private static final String DEFAULT_UNIT = "1";

    @Inject
    private ClinicChargeRepository clinicChargeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ClinicCharge> getClinicChargesByClinicId(String clinicId) {
        return clinicChargeRepository.findByClinicId(clinicId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClinicCharge> getClinicChargeById(String id) {
        ClinicCharge one = clinicChargeRepository.findOne(id);
        return Optional.ofNullable(one);
    }

    @Override
    @Transactional
    public ClinicCharge saveClinicCharge(ClinicCharge charge) {
        // それぞれ変えられるようにしているが、固定値にしておく
        charge.setTaxType(DEFAULT_TAX_TYPE);
        charge.setTaxRate(DEFAULT_TAX_RATE);
        charge.setUnit(DEFAULT_UNIT);
        return clinicChargeRepository.save(charge);
    }

    @Override
    @Transactional
    public ClinicCharge updateClinicCharge(ClinicCharge charge) {
        ClinicCharge one = clinicChargeRepository.findOne(charge.getId());
        ExceptionUtils.throwIfNull(one);

        BeanFactoryUtils.copyNonNullProperties(charge, one);
        return clinicChargeRepository.save(one);
    }

    @Override
    @Transactional
    public void removeClinicCharge(ClinicCharge charge) {
        clinicChargeRepository.delete(charge);
    }
}
