package org.majimena.petz.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petz.common.exceptions.ResourceNotFoundException;
import org.majimena.petz.domain.ClinicInvitation;
import org.majimena.petz.service.ClinicInvitationService;
import org.majimena.petz.web.rest.dto.ClinicInvitationAcceptionDTO;
import org.majimena.petz.web.rest.dto.ClinicInvitationDTO;
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
import java.util.Optional;
import java.util.Set;

/**
 * クリニック招待コントローラ.
 */
@RestController
@RequestMapping("/api")
public class ClinicInvitationController {

    @Inject
    private ClinicInvitationService clinicStaffService;

    @Inject
    private ClinicInvitationDTOValidator clinicInvitationDTOValidator;

    public void setClinicStaffService(ClinicInvitationService clinicStaffService) {
        this.clinicStaffService = clinicStaffService;
    }

    public void setClinicInvitationDTOValidator(ClinicInvitationDTOValidator clinicInvitationDTOValidator) {
        this.clinicInvitationDTOValidator = clinicInvitationDTOValidator;
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
        Optional<ClinicInvitation> staff = clinicStaffService.findClinicInvitationById(invitationId);
        return staff
            .map(s -> ResponseEntity.ok().body(s))
            .orElseThrow(() -> new ResourceNotFoundException("cannot find clinic invitation"));
    }

    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/invitations/{invitationId}", method = RequestMethod.PUT)
    public ResponseEntity<Void> activate(@PathVariable String clinicId, @PathVariable String invitationId,
                                         @Valid @RequestBody ClinicInvitationAcceptionDTO acception) {
        clinicStaffService.activate(acception.getActivationKey());
        return ResponseEntity.ok().build();
    }
}
