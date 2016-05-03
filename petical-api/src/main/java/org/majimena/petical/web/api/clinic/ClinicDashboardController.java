package org.majimena.petical.web.api.clinic;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petical.domain.clinic.ClinicOutline;
import org.majimena.petical.domain.clinic.ClinicOutlineCriteria;
import org.majimena.petical.security.SecurityUtils;
import org.majimena.petical.service.ClinicService;
import org.majimena.petical.web.utils.ErrorsUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.Optional;

/**
 * クリニックダッシュボードのコントローラ.
 */
@RestController
@RequestMapping("/api/v1")
public class ClinicDashboardController {

    /**
     * クリニックサービス.
     */
    @Inject
    private ClinicService clinicService;

    /**
     * クリニック概要を取得する.
     *
     * @param clinicId クリニックID
     * @return レスポンスステータス（正常時は200、権限エラー時は401、該当なしは404）
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/outline", method = RequestMethod.GET)
    public ResponseEntity<ClinicOutline> get(@PathVariable String clinicId, @Valid ClinicOutlineCriteria criteria) {
        // クリニック権限のチェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // クリニック概要を取得
        criteria.setClinicId(clinicId);
        Optional<ClinicOutline> optional = clinicService.findClinicOutlineByClinicOutlineCriteria(criteria);
        return optional
                .map(outline -> ResponseEntity.ok().body(outline))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
