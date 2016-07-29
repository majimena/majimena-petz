package org.majimena.petical.web.api.clinics.tickets;

import com.codahale.metrics.annotation.Timed;
import org.apache.commons.lang3.StringUtils;
import org.majimena.petical.domain.TicketInspection;
import org.majimena.petical.security.SecurityUtils;
import org.majimena.petical.service.TicketInspectionService;
import org.majimena.petical.service.TicketService;
import org.majimena.petical.web.utils.ErrorsUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;

/**
 * チケット検査コントローラ.
 */
@RestController
@RequestMapping("/api/v1/clinics/{clinicId}/tickets/{ticketId}/inspections")
public class TicketInspectionController {

    @Inject
    private TicketService ticketService;

    @Inject
    private TicketInspectionService ticketInspectionService;

    @Timed
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<TicketInspection>> get(@PathVariable String clinicId, @PathVariable String ticketId) {
        // クリニック権限のチェックとIDのコード体系チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        ErrorsUtils.throwIfNotIdentify(ticketId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // チケットの権限チェックをして、問題なければチケット検査の一覧を取得する
        return ticketService.getTicketByTicketId(ticketId)
                .filter(ticket -> StringUtils.equals(ticket.getClinic().getId(), clinicId))
                .map(ticket -> ticketInspectionService.getTicketInspectionsByTicketId(ticket.getId()))
                .map(inspections -> ResponseEntity.ok().body(inspections))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Timed
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<TicketInspection> post(@PathVariable String clinicId, @PathVariable String ticketId, @RequestBody @Valid TicketInspection inspection) {
        // クリニック権限のチェックとIDのコード体系チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        ErrorsUtils.throwIfNotIdentify(ticketId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // TODO マスタチェック以外のValidationを追加

        // チケットの権限チェックをして、問題なければチケットを登録する
        return ticketService.getTicketByTicketId(ticketId)
                .filter(ticket -> StringUtils.equals(ticket.getClinic().getId(), clinicId))
                .map(ticket -> {
                    inspection.setTicket(ticket);
                    return ticketInspectionService.saveTicketInspection(inspection);
                })
                .map(saved -> ResponseEntity.created(URI.create("/api/v1/clinics/" + clinicId + "/tickets/" + ticketId + "/inspections/" + saved.getId() + "/")).body(saved))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Timed
    @RequestMapping(value = "/{inspectionId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable String clinicId, @PathVariable String ticketId, @PathVariable String inspectionId) {
        // クリニック権限のチェックとIDのコード体系チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        ErrorsUtils.throwIfNotIdentify(ticketId);
        ErrorsUtils.throwIfNotIdentify(inspectionId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // チケットの権限チェックをして、問題なければ検査情報を削除する
        return ticketService.getTicketByTicketId(ticketId)
                .filter(ticket -> StringUtils.equals(ticket.getClinic().getId(), clinicId))
                .map(ticket -> {
                    ticketInspectionService.removeTicketInspectionById(inspectionId);
                    return ResponseEntity.ok().build();
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
