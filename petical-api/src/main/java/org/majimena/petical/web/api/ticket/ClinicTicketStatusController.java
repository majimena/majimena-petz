package org.majimena.petical.web.api.ticket;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petical.datatype.TicketState;
import org.majimena.petical.domain.Ticket;
import org.majimena.petical.security.SecurityUtils;
import org.majimena.petical.service.TicketService;
import org.majimena.petical.service.TicketStatusService;
import org.majimena.petical.web.utils.ErrorsUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

/**
 * クリニックが持つチケットのステータスに関するコントローラ.
 */
@RestController
@RequestMapping("/api/v1/clinics/{clinicId}/tickets/{ticketId}/status")
public class ClinicTicketStatusController {

    @Inject
    private TicketService ticketService;

    @Inject
    private TicketStatusService ticketStatusService;

    @Timed
    @RequestMapping(value = "/{status}", method = RequestMethod.PUT)
    public ResponseEntity<Ticket> put(@PathVariable String clinicId, @PathVariable String ticketId, @PathVariable String status) {
        // クリニックの権限チェックとIDのコード体系チェック
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);
        ErrorsUtils.throwIfNotIdentify(ticketId);
        TicketState state = TicketState.valueOf(status);

        // スケジュールを更新する
        return ticketService.getTicketByTicketId(ticketId)
                .filter(ticket -> SecurityUtils.isUserInClinic(ticket.getClinic().getId()))
                .map(ticket -> ticketStatusService.updateTicketStatus(ticket.getId(), state))
                .map(ticket -> ResponseEntity.ok().body(ticket))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
