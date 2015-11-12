package org.majimena.petz.service.impl;

import org.majimena.petz.datatype.TaxType;
import org.majimena.petz.datetime.L10nDateTimeProvider;
import org.majimena.petz.domain.Examination;
import org.majimena.petz.domain.ticket.ExaminationCriteria;
import org.majimena.petz.repository.ExaminationRepository;
import org.majimena.petz.repository.spec.ExaminationSpecs;
import org.majimena.petz.service.ExaminationService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 診察サービスの実装.
 */
@Service
public class ExaminationServiceImpl implements ExaminationService {

    @Inject
    private ExaminationRepository examinationRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Examination> getExaminationsByExaminationCriteria(ExaminationCriteria criteria) {
        List<Examination> examinations = examinationRepository.findAll(ExaminationSpecs.of(criteria));
        return examinations;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Examination> getExaminationByExaminationId(String examinationId) {
        Examination one = examinationRepository.findOne(examinationId);
        return Optional.ofNullable(one);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Examination saveExamination(Examination examination) {
        // 合計額と税額の計算
        BigDecimal rate = examination.getTaxRate();
        BigDecimal total = examination.getPrice().multiply(examination.getQuantity());
        if (examination.getTaxType().is(TaxType.INCLUSIVE)) {
            // 内税計算
            BigDecimal tax = total.divide(BigDecimal.ONE.add(rate)).multiply(rate);
            examination.setTotal(total);
            examination.setTax(tax);
        } else {
            // 外税計算
            BigDecimal tax = total.multiply(rate);
            examination.setTotal(total.add(tax));
            examination.setTax(tax);
        }

        // 不足項目を追加
        examination.setExaminationDateTime(L10nDateTimeProvider.now().toLocalDateTime());
        examination.setRemoved(Boolean.FALSE);

        // 保存する
        return examinationRepository.save(examination);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Examination updateExamination(Examination examination) {
        return saveExamination(examination);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteExaminationByExaminationId(String clinicId, String ticketId, String examinationId) {

    }
}
