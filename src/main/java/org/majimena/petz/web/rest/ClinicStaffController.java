package org.majimena.petz.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petz.domain.ClinicStaff;
import org.majimena.petz.service.ClinicService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.net.URISyntaxException;
import java.util.List;

/**
 * クリニックスタッフのAPIコントローラ.
 */
@RestController
@RequestMapping("/api")
public class ClinicStaffController {

    @Inject
    private ClinicService clinicService;

    public void setClinicService(ClinicService clinicService) {
        this.clinicService = clinicService;
    }

    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/staffs", method = RequestMethod.GET)
    public ResponseEntity<List<ClinicStaff>> query(@PathVariable String clinicId) throws URISyntaxException {
        List<ClinicStaff> staffs = clinicService.getClinicStaffsById(clinicId);
        return ResponseEntity.ok().body(staffs);
    }

    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/staffs/{staffId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable String clinicId, @PathVariable String staffId) {
        clinicService.deleteClinicStaff(clinicId, staffId);
        return ResponseEntity.ok().build();
    }
}
