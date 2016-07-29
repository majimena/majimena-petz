package org.majimena.petical.web.api.clinics.tickets;

import com.codahale.metrics.annotation.Timed;
import org.apache.commons.lang3.StringUtils;
import org.majimena.petical.domain.TicketAccount;
import org.majimena.petical.security.SecurityUtils;
import org.majimena.petical.service.TicketAccountService;
import org.majimena.petical.service.TicketService;
import org.majimena.petical.web.utils.ErrorsUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;

/**
 * チケット会計コントローラ.
 */
@RestController
@RequestMapping("/api/v1/clinics/{clinicId}/tickets/{ticketId}/accounts")
public class TicketAccountController {
    /**
     * チケットサービス.
     */
    @Inject
    private TicketService ticketService;

    /**
     * チケット会計サービス.
     */
    @Inject
    private TicketAccountService ticketAccountService;

    /**
     * チケットの会計情報を取得する.
     *
     * @param clinicId クリニックID
     * @param ticketId チケットID
     * @return レスポンスエンティティ（通常時は200、参照できないチケットの場合は404）
     */
    @Timed
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<TicketAccount>> get(@PathVariable String clinicId, @PathVariable String ticketId,
                                                   @RequestParam(required = false) boolean force) {
        // クリニックの権限チェックとIDのフォーマットチェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        ErrorsUtils.throwIfNotIdentify(ticketId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // 参照可能チケットであればそのチケットの会計情報を取得
        return ticketService.getTicketByTicketId(ticketId)
                .filter(ticket -> StringUtils.equals(ticket.getClinic().getId(), clinicId))
                .map(ticket -> ticketAccountService.getTicketAccountsByTicketId(ticketId, force))
                .map(accounts -> ResponseEntity.ok().body(accounts))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
