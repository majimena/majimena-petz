package org.majimena.petz.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petz.domain.Clinic;
import org.majimena.petz.service.ClinicService;
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
import java.util.List;
import java.util.Set;

/**
 * クリニック招待コントローラ.
 */
@RestController
@RequestMapping("/v1")
public class ClinicInvitationController {

    @Inject
    private ClinicService clinicService;

    @Inject
    private ClinicInvitationDTOValidator clinicInvitationDTOValidator;

    public void setClinicService(ClinicService clinicService) {
        this.clinicService = clinicService;
    }

    public void setClinicInvitationDTOValidator(ClinicInvitationDTOValidator clinicInvitationDTOValidator) {
        this.clinicInvitationDTOValidator = clinicInvitationDTOValidator;
    }

    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/invitations", method = RequestMethod.POST)
    public ResponseEntity<Void> invite(@PathVariable Long clinicId, @Valid @RequestBody ClinicInvitationDTO invitation, BindingResult errors) throws BindException {
        clinicInvitationDTOValidator.validate(invitation, errors);
        if (errors.hasErrors()) {
            throw new BindException(errors);
        }

        Set<String> emails = new HashSet<>(Arrays.asList(invitation.getEmails()));
        clinicService.inviteStaff(clinicId, emails);
        return ResponseEntity.created(URI.create("/v1/clinics/" + clinicId + "/invitations")).build();
    }

    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/invitations/{invitationId}", method = RequestMethod.GET)
    public ResponseEntity<List<Clinic>> getAll(@PathVariable Long clinicId, @PathVariable Long invitationId, @Valid ClinicInvitationAcceptionDTO acception, BindingResult errors) throws BindException {
//        Pageable pageable = PaginationUtil.generatePageRequest(offset, limit);
//        Page<Clinic> page = clinicService.getClinics(new ClinicCriteria(), pageable);
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/clinics", offset, limit);
//        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
        List<Clinic> clinics = null;
        return ResponseEntity.ok().body(clinics);
    }

}
