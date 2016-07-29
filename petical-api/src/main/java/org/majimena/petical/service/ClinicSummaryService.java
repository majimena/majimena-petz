package org.majimena.petical.service;

import org.majimena.petical.domain.graph.Graph;

/**
 * 動物病院売上サービス.
 */
public interface ClinicSummaryService {

    /**
     * 日別売上グラフを作成する.
     *
     * @param clinicId クリニックID
     * @return 日別売上
     */
    Graph createDailySalesGraph(String clinicId);

    /**
     * 月別売上グラフを作成する.
     *
     * @param clinicId クリニックID
     * @return 月別売上
     */
    Graph createMonthlySalesGraph(String clinicId);

    /**
     * 本日分のチケットのグラフデータを取得する.
     *
     * @param clinicId クリニックID
     * @return 本日分のチケットデータ
     */
    Graph createTodaysTicketGraph(String clinicId);

}
