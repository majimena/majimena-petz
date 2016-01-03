package org.majimena.petz.service;

import org.majimena.petz.domain.graph.Graph;

/**
 * 売り上げサービス.
 */
public interface SalesService {

    /**
     * 日別売り上げを取得する.
     *
     * @param clinicId クリニックID
     * @return 日別売り上げ
     */
    Graph getDailySalesByClinicId(String clinicId);
}
