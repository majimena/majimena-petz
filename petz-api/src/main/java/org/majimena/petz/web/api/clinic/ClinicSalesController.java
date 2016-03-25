package org.majimena.petz.web.api.clinic;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petz.domain.graph.Graph;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.ClinicSalesService;
import org.majimena.petz.web.utils.ErrorsUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

/**
 * クリニック売上のコントローラ.
 */
@RestController
@RequestMapping("/api/v1")
public class ClinicSalesController {

    /**
     * 売上サービス.
     */
    @Inject
    private ClinicSalesService clinicSalesService;

    /**
     * クリニックの日別売上高（過去30日分）を取得する.
     *
     * @param clinicId クリニックID
     * @return レスポンスステータス（正常時は200、権限エラー時は401）
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/sales/daily", method = RequestMethod.GET)
    public ResponseEntity<Graph> getDailySalesGraph(@PathVariable String clinicId) {
        // クリニック権限のチェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // クリニックの日別売上を取得
        Graph graph = clinicSalesService.getDailySalesByClinicId(clinicId);
        return ResponseEntity.ok().body(graph);
    }

    /**
     * クリニックの月別売上高（過去12ヶ月分）を取得する.
     *
     * @param clinicId クリニックID
     * @return レスポンスステータス（正常時は200、権限エラー時は401）
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/sales/monthly", method = RequestMethod.GET)
    public ResponseEntity<Graph> getMonthlySalesGraph(@PathVariable String clinicId) {
        // クリニック権限のチェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // クリニックの月別売上を取得
        Graph graph = clinicSalesService.getMonthlySalesByClinicId(clinicId);
        return ResponseEntity.ok().body(graph);
    }
}
