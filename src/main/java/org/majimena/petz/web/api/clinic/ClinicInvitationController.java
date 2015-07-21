package org.majimena.petz.web.api.clinic;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petz.domain.ClinicInvitation;
import org.majimena.petz.domain.clinic.ClinicInvitationAcception;
import org.majimena.petz.domain.clinic.ClinicInvitationRegistry;
import org.majimena.petz.service.ClinicInvitationService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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

    public void setClinicInvitationService(ClinicInvitationService clinicInvitationService) {
        this.clinicInvitationService = clinicInvitationService;
    }

    public void setClinicInvitationRegistryValidator(ClinicInvitationRegistryValidator clinicInvitationRegistryValidator) {
        this.clinicInvitationRegistryValidator = clinicInvitationRegistryValidator;
    }

    public void setClinicInvitationAcceptionValidator(ClinicInvitationAcceptionValidator clinicInvitationAcceptionValidator) {
        this.clinicInvitationAcceptionValidator = clinicInvitationAcceptionValidator;
    }

    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/invitations", method = RequestMethod.POST)
    public ResponseEntity<Void> invite(@PathVariable String clinicId, @Valid @RequestBody ClinicInvitationRegistry registry, BindingResult errors) throws BindException {
        registry.setClinicId(clinicId);
        clinicInvitationRegistryValidator.validate(registry, errors);
        if (errors.hasErrors()) {
            throw new BindException(errors);
        }

        Set<String> emails = new HashSet<>(Arrays.asList(registry.getEmails()));
        clinicInvitationService.inviteStaff(clinicId, emails);
        return ResponseEntity.created(URI.create("/api/v1/clinics/" + clinicId + "/invitations")).build();
    }

    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/invitations/{invitationId}", method = RequestMethod.GET)
    public ResponseEntity<ClinicInvitation> show(@PathVariable String clinicId, @PathVariable String invitationId) {
        ClinicInvitation invitation = clinicInvitationService.findClinicInvitationById(invitationId);
        return ResponseEntity.ok().body(invitation);
    }

    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/invitations/{invitationId}", method = RequestMethod.PUT)
    public ResponseEntity<Void> activate(@PathVariable String clinicId, @PathVariable String invitationId,
                                         @Valid @RequestBody ClinicInvitationAcception acception, BindingResult errors) throws BindException {
        acception.setClinicId(clinicId);
        acception.setClinicInvitationId(invitationId);
        clinicInvitationAcceptionValidator.validate(acception, errors);
        if (errors.hasErrors()) {
            throw new BindException(errors);
        }

        clinicInvitationService.activate(invitationId, acception.getActivationKey());
        return ResponseEntity.ok().build();
    }
}
