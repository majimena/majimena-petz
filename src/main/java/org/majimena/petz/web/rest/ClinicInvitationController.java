package org.majimena.petz.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petz.domain.ClinicInvitation;
import org.majimena.petz.service.ClinicInvitationService;
import org.majimena.petz.web.rest.dto.ClinicInvitationAcceptionDTO;
import org.majimena.petz.web.rest.dto.ClinicInvitationDTO;
import org.majimena.petz.web.validator.ClinicActivationValidator;
import org.majimena.petz.web.validator.ClinicInvitationDTOValidator;
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
    private ClinicInvitationService clinicStaffService;

    @Inject
    private ClinicInvitationDTOValidator clinicInvitationDTOValidator;

    @Inject
    private ClinicActivationValidator clinicActivationValidator;

    public void setClinicStaffService(ClinicInvitationService clinicStaffService) {
        this.clinicStaffService = clinicStaffService;
    }

    public void setClinicInvitationDTOValidator(ClinicInvitationDTOValidator clinicInvitationDTOValidator) {
        this.clinicInvitationDTOValidator = clinicInvitationDTOValidator;
    }

    public void setClinicActivationValidator(ClinicActivationValidator clinicActivationValidator) {
        this.clinicActivationValidator = clinicActivationValidator;
    }

    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/invitations", method = RequestMethod.POST)
    public ResponseEntity<Void> invite(@PathVariable String clinicId, @Valid @RequestBody ClinicInvitationDTO invitation, BindingResult errors) throws BindException {
        clinicInvitationDTOValidator.validate(invitation, errors);
        if (errors.hasErrors()) {
            throw new BindException(errors);
        }

        Set<String> emails = new HashSet<>(Arrays.asList(invitation.getEmails()));
        clinicStaffService.inviteStaff(clinicId, emails);
        return ResponseEntity.created(URI.create("/api/clinics/" + clinicId + "/invitations")).build();
    }

    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/invitations/{invitationId}", method = RequestMethod.GET)
    public ResponseEntity<ClinicInvitation> show(@PathVariable String clinicId, @PathVariable String invitationId) {
        ClinicInvitation invitation = clinicStaffService.findClinicInvitationById(invitationId);
        return ResponseEntity.ok().body(invitation);
    }

    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/invitations/{invitationId}", method = RequestMethod.PUT)
    public ResponseEntity<Void> activate(@PathVariable String clinicId, @PathVariable String invitationId,
                                         @Valid @RequestBody ClinicInvitationAcceptionDTO acception, BindingResult errors) throws BindException {
        clinicActivationValidator.validate(invitationId, errors);
        if (errors.hasErrors()) {
            throw new BindException(errors);
        }

        clinicStaffService.activate(invitationId, acception.getActivationKey());
        return ResponseEntity.ok().build();
    }
}
