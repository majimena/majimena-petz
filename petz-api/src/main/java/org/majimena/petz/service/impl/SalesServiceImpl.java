package org.majimena.petz.service.impl;

import org.majimena.petz.common.utils.DateTimeUtils;
import org.majimena.petz.datetime.L10nDateTimeProvider;
import org.majimena.petz.domain.graph.Graph;
import org.majimena.petz.repository.InvoiceRepository;
import org.majimena.petz.service.SalesService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 売り上げサービスの実装.
 */
@Service
public class SalesServiceImpl implements SalesService {

    /**
     * 請求書リポジトリ.
     */
    @Inject
    private InvoiceRepository invoiceRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Graph getDailySalesByClinicId(String clinicId) {
        ZonedDateTime start = L10nDateTimeProvider.now().minusDays(30);

        List<List<Object>> data = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            LocalDateTime date = start.plusDays(i).toLocalDateTime();
            LocalDateTime from = DateTimeUtils.minOfDay(date);
            LocalDateTime to = DateTimeUtils.maxOfDay(date);
            BigDecimal sales = invoiceRepository.sumTotal(clinicId, from, to).orElse(BigDecimal.ZERO);
            data.add(Arrays.asList(from.toEpochSecond(ZoneOffset.UTC) * 1000, sales));
        }
        return new Graph(Arrays.asList("Time", "Sales"), data);
    }
}
