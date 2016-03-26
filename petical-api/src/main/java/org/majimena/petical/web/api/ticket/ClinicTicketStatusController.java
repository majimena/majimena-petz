package org.majimena.petical.web.api.ticket;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petical.domain.Ticket;
import org.majimena.petical.security.SecurityUtils;
import org.majimena.petical.service.TicketService;
import org.majimena.petical.web.utils.ErrorsUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

/**
 * クリニックが持つチケットのステータスに関するコントローラ.
 */
@RestController
@RequestMapping("/api/v1")
public class ClinicTicketStatusController {

    @Inject
    private TicketService ticketService;

    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/tickets/{ticketId}/statuses", method = RequestMethod.POST)
    public ResponseEntity<Ticket> put(@PathVariable String clinicId, @PathVariable String ticketId) {
        // クリニックの権限チェックとIDのコード体系チェック
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);
        ErrorsUtils.throwIfNotIdentify(ticketId);

        // スケジュールを更新する
        Ticket created = ticketService.signalTicketStatus(ticketId);
        return ResponseEntity.ok().body(created);
    }
}
