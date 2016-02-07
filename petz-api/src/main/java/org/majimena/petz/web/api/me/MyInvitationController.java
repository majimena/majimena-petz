package org.majimena.petz.web.api.me;

import com.codahale.metrics.annotation.Timed;
import org.apache.commons.lang3.StringUtils;
import org.majimena.petz.domain.ClinicInvitation;
import org.majimena.petz.domain.clinic.ClinicInvitationAcception;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.ClinicInvitationService;
import org.majimena.petz.web.api.clinic.ClinicInvitationAcceptionValidator;
import org.majimena.petz.web.utils.ErrorsUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;

/**
 * 招待状コントローラ.
 */
@RestController
@RequestMapping("/api/v1/me/invitations")
public class MyInvitationController {

    /**
     * クリニック招待サービス.
     */
    @Inject
    private ClinicInvitationService clinicInvitationService;

    /**
     * クリニック招待承認バリデータ.
     */
    @Inject
    private ClinicInvitationAcceptionValidator clinicInvitationAcceptionValidator;

    /**
     * 自分に届いている全ての招待状を取得する.
     *
     * @return レスポンスエンティティ（通常時は200）
     */
    @Timed
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ClinicInvitation>> get() {
        String userId = SecurityUtils.getCurrentUserId();
        List<ClinicInvitation> invitations = clinicInvitationService.getClinicInvitationsByUserId(userId);
        return ResponseEntity.ok().body(invitations);
    }

    /**
     * 自分に届いている招待状を取得する.
     *
     * @return レスポンスエンティティ（通常時は200、見つからない場合は404）
     */
    @Timed
    @RequestMapping(value = "/{invitationId}", method = RequestMethod.GET)
    public ResponseEntity<ClinicInvitation> get(@PathVariable String invitationId) {
        // パラメータをチェックする
        ErrorsUtils.throwIfNotIdentify(invitationId);

        // 自分宛の招待状を取得する
        String userId = SecurityUtils.getCurrentUserId();
        return clinicInvitationService.getClinicInvitationById(invitationId)
                .filter(invitation -> StringUtils.equals(userId, invitation.getInvitedUser().getId()))
                .map(invitation -> ResponseEntity.ok().body(invitation))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Timed
    @RequestMapping(value = "/{invitationId}", method = RequestMethod.POST)
    public ResponseEntity<Void> post(@PathVariable String invitationId,
                                     @Valid @RequestBody ClinicInvitationAcception acception, BindingResult errors) throws BindException {
        // パラメータをチェックする
        ErrorsUtils.throwIfNotIdentify(invitationId);
        acception.setClinicInvitationId(invitationId);

        // カスタムバリデーションを行う
        clinicInvitationAcceptionValidator.validate(acception, errors);
        ErrorsUtils.throwIfHasErrors(errors);

        // 招待をアクティベートする
        clinicInvitationService.activate(invitationId, acception.getActivationKey());
        return ResponseEntity.ok().build();
    }
}
