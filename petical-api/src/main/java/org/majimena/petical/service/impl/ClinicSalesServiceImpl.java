package org.majimena.petical.service.impl;

import org.majimena.petical.common.utils.DateTimeUtils;
import org.majimena.petical.datetime.L10nDateTimeProvider;
import org.majimena.petical.domain.graph.Graph;
import org.majimena.petical.repository.InvoiceRepository;
import org.majimena.petical.service.ClinicSalesService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 売り上げサービスの実装.
 */
@Service
public class ClinicSalesServiceImpl implements ClinicSalesService {

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

            Object[] results = (Object[]) invoiceRepository.sumTotal(clinicId, from, to);
            data.add(Arrays.asList(from.toEpochSecond(ZoneOffset.UTC) * 1000, results[0], results[1]));
        }
        return new Graph(Arrays.asList("Time", "Sales"), data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Graph getMonthlySalesByClinicId(String clinicId) {
        ZonedDateTime start = L10nDateTimeProvider.now().minusMonths(12);
        List<List<Object>> data = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            LocalDateTime date = start.plusMonths(i).toLocalDateTime();
            LocalDate first = YearMonth.from(date).atDay(1);
            LocalDate last = YearMonth.from(date).atEndOfMonth();

            LocalDateTime from = DateTimeUtils.minOfDay(first);
            LocalDateTime to = DateTimeUtils.maxOfDay(last);

            Object[] results = (Object[]) invoiceRepository.sumTotal(clinicId, from, to);
            data.add(Arrays.asList(from.toEpochSecond(ZoneOffset.UTC) * 1000, results[0], results[1]));
        }
        return new Graph(Arrays.asList("Time", "Sales"), data);
    }
}
