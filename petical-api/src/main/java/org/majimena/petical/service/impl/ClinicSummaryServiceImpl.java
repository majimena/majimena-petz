package org.majimena.petical.service.impl;

import org.majimena.petical.datatype.TicketState;
import org.majimena.petical.datetime.L10nDateTimeProvider;
import org.majimena.petical.domain.graph.Graph;
import org.majimena.petical.repository.TicketRepository;
import org.majimena.petical.service.ClinicSummaryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Comparator.comparing;

/**
 * 売り上げサービスの実装.
 */
@Service
public class ClinicSummaryServiceImpl implements ClinicSummaryService {
    /**
     * チケットリポジトリ.
     */
    @Inject
    private TicketRepository ticketRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Graph createDailySalesGraph(String clinicId) {
        // 29日前からの日付範囲指定とする
        ZonedDateTime now = L10nDateTimeProvider.today();
        LocalDateTime start = now.minusDays(29L).toLocalDateTime();
        LocalDateTime end = now.toLocalDateTime();

        // 日別売上と診察数のサマリを取得する
        List<Object[]> results = ticketRepository.sumDailySalesAndCount(clinicId, start.format(ISO_DATE_TIME), end.format(ISO_DATE_TIME));
        List<List<Object>> data = results.stream()
                .map(objects -> Arrays.asList(objects[0], objects[1], objects[2]))
                .collect(Collectors.toList());

        // 売上がない日は0埋めしたオブジェクトにする
        IntStream.range(0, 29).boxed()
                .map(integer -> now.minusDays(integer).format(ofPattern("yyyy-MM-dd")))
                .filter(target -> !contains(data, target))
                .forEach(s -> data.add(Arrays.asList(s, BigDecimal.ZERO, BigDecimal.ZERO)));

        // ソートしてから返す
        data.sort(comparing(objects -> objects.get(0).toString()));
        return new Graph(Arrays.asList("Date", "Sales", "Count"), data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Graph createMonthlySalesGraph(String clinicId) {
        // 11ヶ月前からの日付範囲指定とする
        ZonedDateTime now = L10nDateTimeProvider.today();
        LocalDateTime start = now.minusMonths(11).toLocalDateTime();
        LocalDateTime end = now.plusMonths(1).withDayOfMonth(1).toLocalDateTime();

        // 月別売上と診察数のサマリを取得する
        List<Object[]> results = ticketRepository.sumMonthlySalesAndCount(clinicId, start.format(ISO_DATE_TIME), end.format(ISO_DATE_TIME));
        List<List<Object>> data = results.stream()
                .map(objects -> Arrays.asList(objects[0] + "-01", objects[1], objects[2]))
                .collect(Collectors.toList());

        // 売上がない月は0埋めしたオブジェクトにする
        IntStream.range(0, 11).boxed()
                .map(integer -> now.minusMonths(integer).format(ofPattern("yyyy-MM")) + "-01")
                .filter(target -> !contains(data, target))
                .forEach(s -> data.add(Arrays.asList(s, BigDecimal.ZERO, BigDecimal.ZERO)));

        // ソートしてから返す
        data.sort(comparing(objects -> objects.get(0).toString()));
        return new Graph(Arrays.asList("Date", "Sales", "Count"), data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Graph createTodaysTicketGraph(String clinicId) {
        ZonedDateTime now = L10nDateTimeProvider.today();
        LocalDateTime start = now.withHour(0).withMinute(0).toLocalDateTime();
        LocalDateTime end = now.withHour(23).withMinute(0).toLocalDateTime();

        // 時間軸のLISTを生成
        List<List<Object>> data = IntStream.range(0, 23).boxed()
                .map(integer -> now.withHour(integer).withMinute(0).format(ofPattern("yyyy-MM-dd HH:mm")))
                .map(datetime -> Arrays.asList((Object) datetime, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO)) // 日時、完了数、未了数、キャンセル数の順番
                .collect(Collectors.toList());

        // 未了チケット数を取得してマージ
        List<Object[]> reserved = ticketRepository.countNotStateTickets(clinicId, start.format(ISO_DATE_TIME), end.format(ISO_DATE_TIME), TicketState.COMPLETED.name(), TicketState.CANCEL.name());
        reserved.stream()
                .forEach(objects -> append(data, objects[0].toString(), list -> list.set(1, objects[1])));

        // 完了済みチケット数を取得してマージ
        List<Object[]> completes = ticketRepository.countStateTickets(clinicId, start.format(ISO_DATE_TIME), end.format(ISO_DATE_TIME), TicketState.COMPLETED.name());
        completes.stream()
                .forEach(objects -> append(data, objects[0].toString(), list -> list.set(2, objects[1])));

        // キャンセルチケット数を取得してマージ
        List<Object[]> cancels = ticketRepository.countStateTickets(clinicId, start.format(ISO_DATE_TIME), end.format(ISO_DATE_TIME), TicketState.CANCEL.name());
        cancels.stream()
                .forEach(objects -> append(data, objects[0].toString(), list -> list.set(3, objects[1])));

        // ソートしてから返す
        data.sort(comparing(objects -> objects.get(0).toString()));
        data.forEach(objects -> objects.set(0, L10nDateTimeProvider.from(objects.get(0).toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
        return new Graph(Arrays.asList("Time", "Completed", "Canceled", "Reserved"), data);
    }

    protected boolean contains(List<List<Object>> data, String target) {
        return data.stream()
                .filter(objects -> objects.contains(target))
                .findFirst()
                .isPresent();
    }

    protected void append(List<List<Object>> data, String datetime, Consumer<List<Object>> consumer) {
        data.stream()
                .filter(objects -> objects.contains(datetime))
                .forEach(objects -> consumer.accept(objects));
    }
}
