package org.majimena.petical.service;

import org.majimena.petical.common.exceptions.ApplicationException;
import org.majimena.petical.domain.Chart;
import org.majimena.petical.domain.chart.ChartCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * カルテサービス.
 */
public interface ChartService {

    /**
     * 飼い主IDをもとにカルテを取得する.
     *
     * @param customerId 飼い主ID
     * @return カルテ一覧
     */
    List<Chart> getChartsByCustomerId(String customerId);

    /**
     * クリニックIDをもとにカルテを取得する.
     *
     * @param clinicId クリニックID
     * @return カルテ一覧
     */
    List<Chart> getChartsByClinicId(String clinicId);

    /**
     * カルテを検索する. ページングに対応するため, ページ情報が必要.
     *
     * @param criteria カルテ検索条件
     * @param pageable ページ情報
     * @return 検索条件に該当するカルテの一覧
     */
    @Deprecated
    Page<Chart> findChartsByChartCriteria(ChartCriteria criteria, Pageable pageable);

    /**
     * カルテIDをもとに, カルテを取得する.
     *
     * @param clinicId クリニックID
     * @param chartId  カルテID
     * @return カルテ
     */
    Optional<Chart> getChartByChartId(String clinicId, String chartId);

    /**
     * カルテを保存する. 未登録のペット情報があれば, それも一緒に登録する.
     *
     * @param chart カルテ情報
     * @return 保存しカルテ情報
     * @throws ApplicationException
     */
    Chart saveChart(Chart chart);

    Chart updateChart(Chart chart);

    void deleteChart(Chart chart);
}
