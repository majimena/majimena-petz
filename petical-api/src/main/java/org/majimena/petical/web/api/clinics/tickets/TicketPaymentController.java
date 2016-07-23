package org.majimena.petical.web.api.clinics.tickets;

import com.codahale.metrics.annotation.Timed;
import org.apache.commons.lang3.StringUtils;
import org.majimena.petical.domain.TicketPayment;
import org.majimena.petical.security.SecurityUtils;
import org.majimena.petical.service.TicketPaymentService;
import org.majimena.petical.service.TicketService;
import org.majimena.petical.web.utils.ErrorsUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
 * チケット支払コントローラ.
 */
@RestController
@RequestMapping("/api/v1/clinics/{clinicId}/tickets/{ticketId}/payments")
public class TicketPaymentController {
    /**
     * チケットサービス.
     */
    @Inject
    private TicketService ticketService;

    /**
     * チケット支払サービス.
     */
    @Inject
    private TicketPaymentService ticketPaymentService;

    /**
     * チケットの支払情報を取得する.
     *
     * @param clinicId クリニックID
     * @param ticketId チケットID
     * @return レスポンスエンティティ（通常時は200、参照できないチケットの場合は404）
     */
    @Timed
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<TicketPayment>> get(@PathVariable String clinicId, @PathVariable String ticketId) {
        // クリニックの権限チェックとIDのフォーマットチェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        ErrorsUtils.throwIfNotIdentify(ticketId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // 参照可能チケットであればそのチケットの支払情報を取得
        return ticketService.getTicketByTicketId(ticketId)
                .filter(ticket -> StringUtils.equals(ticket.getClinic().getId(), clinicId))
                .map(ticket -> ticketPaymentService.getTicketPaymentsByTicketId(ticketId))
                .map(payments -> ResponseEntity.ok().body(payments))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * チケットの支払情報を登録する.
     *
     * @param clinicId クリニックID
     * @param ticketId チケットID
     * @param payment  チケット支払情報
     * @return レスポンスエンティティ（通常時は201、参照できないチケットの場合は404）
     */
    @Timed
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<TicketPayment> post(@PathVariable String clinicId, @PathVariable String ticketId, @RequestBody @Valid TicketPayment payment) {
        // クリニックの権限チェックとIDのフォーマットチェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        ErrorsUtils.throwIfNotIdentify(ticketId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // 参照可能チケットであればそのチケットの支払情報を取得
        return ticketService.getTicketByTicketId(ticketId)
                .filter(ticket -> StringUtils.equals(ticket.getClinic().getId(), clinicId))
                .map(ticket -> {
                    payment.setTicket(ticket);
                    return ticketPaymentService.saveTicketPayment(payment);
                })
                .map(payments -> ResponseEntity.created(URI.create("/api/clinics/" + clinicId + "/tickets/" + ticketId + "/payments")).body(payments))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
