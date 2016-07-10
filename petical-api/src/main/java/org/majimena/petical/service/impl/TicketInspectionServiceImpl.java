package org.majimena.petical.service.impl;

import org.majimena.petical.common.provider.JsonProvider;
import org.majimena.petical.common.utils.ExceptionUtils;
import org.majimena.petical.domain.ClinicInspection;
import org.majimena.petical.domain.TicketInspection;
import org.majimena.petical.repository.ClinicInspectionRepository;
import org.majimena.petical.repository.TicketInspectionRepository;
import org.majimena.petical.service.TicketInspectionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

/**
 * チケット検査サービスの実装.
 */
@Service
public class TicketInspectionServiceImpl implements TicketInspectionService {

    /**
     * チケット検査リポジトリ.
     */
    @Inject
    private TicketInspectionRepository ticketInspectionRepository;

    /**
     * 動物病院向け検査リポジトリ.
     */
    @Inject
    private ClinicInspectionRepository clinicInspectionRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<TicketInspection> getTicketInspectionsByTicketId(String ticketId) {
        List<TicketInspection> inspections = ticketInspectionRepository.findByTicketIdOrderByCreatedDateAsc(ticketId);
        return inspections;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public TicketInspection saveTicketInspection(TicketInspection inspection) {
        // 料金を取得する
        ClinicInspection i = clinicInspectionRepository.findOne(inspection.getClinicInspection().getId());
        ExceptionUtils.throwIfNull(i);

        // 検査の明細を計算する
        BigDecimal amount = i.getPrice().multiply(inspection.getQuantity());
        BigDecimal tax = amount.multiply(i.getTaxRate()).setScale(0, BigDecimal.ROUND_DOWN);
        inspection.setClinicInspection(i);
        inspection.setName(i.getName());
        inspection.setPrice(i.getPrice());
        inspection.setAmount(amount);
        inspection.setTax(tax);
        inspection.setSubtotal(amount.add(tax));
        inspection.setOriginal(JsonProvider.toJson(i));

        return ticketInspectionRepository.save(inspection);
    }
}
