package org.majimena.petz.web.api.clinic;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petz.domain.clinic.ClinicOutline;
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
    public ResponseEntity<ClinicOutline> get(@PathVariable String clinicId) {
        // クリニック権限のチェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // クリニック概要を取得
        Optional<ClinicOutline> optional = clinicService.getClinicOutlineByClinicId(clinicId);
        return optional
                .map(outline -> new ResponseEntity<>(outline, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
