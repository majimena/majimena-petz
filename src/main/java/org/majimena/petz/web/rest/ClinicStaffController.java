package org.majimena.petz.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.clinic.ClinicCriteria;
import org.majimena.petz.service.ClinicService;
import org.majimena.petz.web.rest.dto.ClinicInvitationDTO;
import org.majimena.petz.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * クリニックコントローラ.
 */
@RestController
@RequestMapping("/api")
public class ClinicStaffController {

    private final Logger logger = LoggerFactory.getLogger(ClinicStaffController.class);

    @Inject
    private ClinicService clinicService;

    public void setClinicService(ClinicService clinicService) {
        this.clinicService = clinicService;
    }

    @Timed
    @RequestMapping(value = "/clinics/{id}/staffs", method = RequestMethod.GET)
    public ResponseEntity<List<Clinic>> getAll(@PathVariable Long id) {
//        Pageable pageable = PaginationUtil.generatePageRequest(offset, limit);
//        Page<Clinic> page = clinicService.getClinics(new ClinicCriteria(), pageable);
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/clinics", offset, limit);
//        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
        List<Clinic> clinics = null;
        return ResponseEntity.ok().body(clinics);
    }

    @Timed
    @RequestMapping(value = "/clinics/{id}/staffs", method = RequestMethod.POST)
    public ResponseEntity<Void> invite(@PathVariable Long id, @Valid @RequestBody ClinicInvitationDTO invitation) {
        clinicService.deleteClinic(id);
        return ResponseEntity.ok().build();
    }

}
