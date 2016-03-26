package org.majimena.petical.web.api.chart;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petical.domain.Ticket;
import org.majimena.petical.domain.ticket.ClinicChartTicketCriteria;
import org.majimena.petical.security.SecurityUtils;
import org.majimena.petical.service.TicketService;
import org.majimena.petical.web.utils.ErrorsUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;

/**
 * クリニックカルテチケットコントローラ.
 */
@RestController
@RequestMapping("/api/v1")
public class ClinicChartTicketController {

    /**
     * チケットサービス.
     */
    @Inject
    private TicketService ticketService;

    /**
     * カルテを検索する.
     *
     * @param clinicId クリニックID
     * @param chartId  カルテID
     * @return レスポンスエンティティ（通常時は200）
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/charts/{chartId}/tickets", method = RequestMethod.GET)
    public ResponseEntity<List<Ticket>> getAll(@PathVariable String clinicId, @PathVariable String chartId,
                                               @Valid ClinicChartTicketCriteria criteria) {
        // クリニックの権限チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        ErrorsUtils.throwIfNotIdentify(chartId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // カルテを検索してチケットを取得する
        criteria.setClinicId(clinicId);
        criteria.setChartId(chartId);
        List<Ticket> tickets = ticketService.getTicketsByClinicChartTicketCriteria(criteria);
        return ResponseEntity.ok().body(tickets);
    }
}
