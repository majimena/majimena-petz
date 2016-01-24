package org.majimena.petz.web.api.clinic;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petz.domain.ClinicInvitation;
import org.majimena.petz.domain.clinic.ClinicInvitationAcception;
import org.majimena.petz.domain.clinic.ClinicInvitationRegistry;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.ClinicInvitationService;
import org.majimena.petz.web.utils.ErrorsUtils;
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
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * クリニック招待コントローラ.
 */
@RestController
@RequestMapping("/api/v1")
public class ClinicInvitationController {

    @Inject
    private ClinicInvitationService clinicInvitationService;

    @Inject
    private ClinicInvitationRegistryValidator clinicInvitationRegistryValidator;

    @Inject
    private ClinicInvitationAcceptionValidator clinicInvitationAcceptionValidator;

    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/invitations", method = RequestMethod.POST)
    public ResponseEntity<Void> invite(@PathVariable String clinicId, @Valid @RequestBody ClinicInvitationRegistry registry, BindingResult errors) throws BindException {
        // クリニックの権限チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // カスタムバリデーションを行う
        registry.setClinicId(clinicId);
        clinicInvitationRegistryValidator.validate(registry, errors);
        ErrorsUtils.throwIfHasErrors(errors);

        // 招待状を送る
        Set<String> emails = new HashSet<>(Arrays.asList(registry.getEmails()));
        clinicInvitationService.inviteStaff(clinicId, emails);
        return ResponseEntity.created(URI.create("/api/v1/clinics/" + clinicId + "/invitations")).build();
    }

    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/invitations/{invitationId}", method = RequestMethod.GET)
    public ResponseEntity<ClinicInvitation> get(@PathVariable String clinicId, @PathVariable String invitationId) {
        // クリニックの権限チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // 招待状を取得する
        ClinicInvitation invitation = clinicInvitationService.findClinicInvitationById(invitationId);
        return ResponseEntity.ok().body(invitation);
    }

    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/invitations/{invitationId}", method = RequestMethod.PUT)
    public ResponseEntity<Void> activate(@PathVariable String clinicId, @PathVariable String invitationId,
                                         @Valid @RequestBody ClinicInvitationAcception acception, BindingResult errors) throws BindException {
        // クリニックの権限チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        ErrorsUtils.throwIfNotIdentify(invitationId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // カスタムバリデーションを行う
        acception.setClinicId(clinicId);
        acception.setClinicInvitationId(invitationId);
        clinicInvitationAcceptionValidator.validate(acception, errors);
        ErrorsUtils.throwIfHasErrors(errors);

        // 招待をアクティベートする
        clinicInvitationService.activate(invitationId, acception.getActivationKey());
        return ResponseEntity.ok().build();
    }
}
