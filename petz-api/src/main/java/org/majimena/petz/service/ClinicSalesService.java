package org.majimena.petz.service;

import org.majimena.petz.domain.graph.Graph;

/**
 * 売り上げサービス.
 */
public interface ClinicSalesService {

    /**
     * 日別売上を取得する.
     *
     * @param clinicId クリニックID
     * @return 日別売上
     */
    Graph getDailySalesByClinicId(String clinicId);

    /**
     * 月別売上を取得する
     *
     * @param clinicId クリニックID
     * @return 月別売上
     */
    Graph getMonthlySalesByClinicId(String clinicId);

}
