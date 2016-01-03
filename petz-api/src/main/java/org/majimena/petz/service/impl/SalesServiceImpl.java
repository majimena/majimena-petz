package org.majimena.petz.service.impl;

import org.majimena.petz.common.utils.DateTimeUtils;
import org.majimena.petz.datatype.InvoiceState;
import org.majimena.petz.datetime.L10nDateTimeProvider;
import org.majimena.petz.domain.graph.Graph;
import org.majimena.petz.repository.InvoiceRepository;
import org.majimena.petz.service.SalesService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 売り上げサービスの実装.
 */
@Service
public class SalesServiceImpl implements SalesService {

    @Inject
    private InvoiceRepository invoiceRepository;

    @Override
    @Transactional(readOnly = true)
    public Graph getDailySalesByClinicId(String clinicId) {
        ZonedDateTime start = L10nDateTimeProvider.now().minusDays(30);

        List<String> labels = new ArrayList<>();
        List<BigDecimal> values = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            LocalDateTime date = start.plusDays(i).toLocalDateTime();
            LocalDateTime from = DateTimeUtils.minOfDay(date);
            LocalDateTime to = DateTimeUtils.maxOfDay(date);
            BigDecimal sales = invoiceRepository.sumTotal(clinicId, InvoiceState.PAID, from, to).orElse(BigDecimal.ZERO);
            labels.add(from.format(DateTimeFormatter.ofPattern("dd")));
            values.add(sales);
        }

        return new Graph(labels, Arrays.asList(values));
    }
}
