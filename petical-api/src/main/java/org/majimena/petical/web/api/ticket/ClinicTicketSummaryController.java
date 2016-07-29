package org.majimena.petical.web.api.ticket;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petical.domain.graph.Graph;
import org.majimena.petical.security.SecurityUtils;
import org.majimena.petical.service.ClinicSummaryService;
import org.majimena.petical.web.utils.ErrorsUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

/**
 * クリニックチケットのサマリのコントローラ.
 */
@RestController
@RequestMapping("/api/v1")
public class ClinicTicketSummaryController {
    /**
     * 動物病院サマリサービス.
     */
    @Inject
    private ClinicSummaryService clinicSummaryService;

    /**
     * クリニックのチケットのサマリを取得する.
     *
     * @param clinicId クリニックID
     * @return レスポンスステータス（正常時は200、権限エラー時は401）
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/tickets/summary/daily", method = RequestMethod.GET)
    public ResponseEntity<Graph> get(@PathVariable String clinicId) {
        // クリニック権限のチェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // 本日分のチケットのサマリを取得
        Graph graph = clinicSummaryService.createTodaysTicketGraph(clinicId);
        return ResponseEntity.ok().body(graph);
    }
}
