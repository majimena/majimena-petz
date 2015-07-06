package org.majimena.petz.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petz.domain.ClinicStaff;
import org.majimena.petz.service.ClinicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.net.URISyntaxException;
import java.util.List;

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
    @RequestMapping(value = "/clinics/{clinicId}/staffs", method = RequestMethod.GET)
    public ResponseEntity<List<ClinicStaff>> query(@PathVariable Long clinicId) throws URISyntaxException {
//        Pageable pageable = PaginationUtil.generatePageRequest(offset, limit);
//        Page<Clinic> page = clinicService.getClinics(new ClinicCriteria(), pageable);
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/clinics", offset, limit);
        List<ClinicStaff> staffs = null;
        return ResponseEntity.ok().body(staffs);
    }

    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/staffs/{staffId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable Long clinicId, @PathVariable Long staffId) {
        return ResponseEntity.noContent().build();
    }

}
