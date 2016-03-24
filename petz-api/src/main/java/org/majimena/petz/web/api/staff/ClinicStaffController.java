package org.majimena.petz.web.api.staff;

import com.codahale.metrics.annotation.Timed;
import org.apache.commons.lang3.StringUtils;
import org.majimena.petz.domain.ClinicStaff;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.ClinicService;
import org.majimena.petz.web.utils.ErrorsUtils;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/v1")
public class ClinicStaffController {

    @Inject
    private ClinicService clinicService;

    public void setClinicService(ClinicService clinicService) {
        this.clinicService = clinicService;
    }

    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/staffs", method = RequestMethod.GET)
    public ResponseEntity<List<ClinicStaff>> get(@PathVariable String clinicId) throws URISyntaxException {
        List<ClinicStaff> staffs = clinicService.getClinicStaffsById(clinicId);
        return ResponseEntity.ok().body(staffs);
    }

    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/staffs/{staffId}", method = RequestMethod.GET)
    public ResponseEntity<ClinicStaff> get(@PathVariable String clinicId, @PathVariable String staffId) {
        // クリニックの権限チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        ErrorsUtils.throwIfNotIdentify(staffId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // スタッフを検索して権限をチェックする
        return clinicService.getClinicStaffById(staffId)
                .filter(staff -> StringUtils.equals(clinicId, staff.getClinic().getId()))
                .map(staff -> ResponseEntity.ok().body(staff))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/staffs/{staffId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable String clinicId, @PathVariable String staffId) {
        // TODO 権限チェックが必要
        clinicService.deleteClinicStaff(clinicId, staffId);
        return ResponseEntity.ok().build();
    }
}
