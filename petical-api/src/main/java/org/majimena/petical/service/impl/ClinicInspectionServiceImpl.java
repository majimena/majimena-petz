package org.majimena.petical.service.impl;

import org.majimena.petical.common.utils.BeanFactoryUtils;
import org.majimena.petical.common.utils.ExceptionUtils;
import org.majimena.petical.datatype.TaxType;
import org.majimena.petical.domain.ClinicInspection;
import org.majimena.petical.repository.ClinicInspectionRepository;
import org.majimena.petical.service.ClinicInspectionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 動物病院検査サービスの実装.
 */
@Service
public class ClinicInspectionServiceImpl implements ClinicInspectionService {

    private static final TaxType DEFAULT_TAX_TYPE = TaxType.EXCLUSIVE;

    private static final BigDecimal DEFAULT_TAX_RATE = BigDecimal.valueOf(0.08);

    private static final String DEFAULT_UNIT = "1";

    @Inject
    private ClinicInspectionRepository clinicInspectionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ClinicInspection> getClinicChargesByClinicId(String clinicId) {
        return clinicInspectionRepository.findByClinicId(clinicId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClinicInspection> getClinicChargeById(String id) {
        ClinicInspection one = clinicInspectionRepository.findOne(id);
        return Optional.ofNullable(one);
    }

    @Override
    @Transactional
    public ClinicInspection saveClinicCharge(ClinicInspection inspection) {
        // それぞれ変えられるようにしているが、固定値にしておく
        inspection.setTaxType(DEFAULT_TAX_TYPE);
        inspection.setTaxRate(DEFAULT_TAX_RATE);
        inspection.setUnit(DEFAULT_UNIT);
        return clinicInspectionRepository.save(inspection);
    }

    @Override
    @Transactional
    public ClinicInspection updateClinicCharge(ClinicInspection charge) {
        ClinicInspection one = clinicInspectionRepository.findOne(charge.getId());
        ExceptionUtils.throwIfNull(one);

        BeanFactoryUtils.copyNonNullProperties(charge, one);
        return clinicInspectionRepository.save(one);
    }

    @Override
    @Transactional
    public void removeClinicCharge(ClinicInspection charge) {
        clinicInspectionRepository.delete(charge);
    }
}
