package org.majimena.petz.web.api.clinic;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.clinic.ClinicCriteria;
import org.majimena.petz.service.ClinicService;
import org.majimena.petz.web.rest.util.PaginationUtil;
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
@RequestMapping("/api/v1")
public class ClinicController {

    @Inject
    private ClinicService clinicService;

    public void setClinicService(ClinicService clinicService) {
        this.clinicService = clinicService;
    }

    @Timed
    @RequestMapping(value = "/clinics", method = RequestMethod.POST)
    public ResponseEntity<Clinic> create(@Valid @RequestBody Clinic clinic) {
        Clinic saved = clinicService.saveClinic(clinic);
        return ResponseEntity.created(URI.create("/api/v1/clinics/" + saved.getId())).body(saved);
    }

    @Timed
    @RequestMapping(value = "/clinics/{clinicId}", method = RequestMethod.PUT)
    public ResponseEntity<Clinic> update(@PathVariable String clinicId, @Valid @RequestBody Clinic clinic) {
        clinic.setId(clinicId);
        clinicService.updateClinic(clinic);
        return ResponseEntity.ok().body(clinic);
    }

    @Timed
    @RequestMapping(value = "/clinics", method = RequestMethod.GET)
    public ResponseEntity<List<Clinic>> getAll(@RequestParam(value = "page", required = false) Integer offset,
                                               @RequestParam(value = "per_page", required = false) Integer limit) throws URISyntaxException {
        Pageable pageable = PaginationUtil.generatePageRequest(offset, limit);
        Page<Clinic> page = clinicService.getClinics(new ClinicCriteria(), pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/v1/clinics", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @Timed
    @RequestMapping(value = "/clinics/{id}", method = RequestMethod.GET)
    public ResponseEntity<Clinic> get(@PathVariable String id) {
        Optional<Clinic> one = clinicService.getClinicById(id);
        return one
            .map(clinic -> new ResponseEntity<>(clinic, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Timed
    @RequestMapping(value = "/clinics/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable String id) {
        clinicService.deleteClinic(id);
        return ResponseEntity.ok().build();
    }

}
