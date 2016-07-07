package org.majimena.petical.web.api.clinics.medicines;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petical.domain.Medicine;
import org.majimena.petical.security.SecurityUtils;
import org.majimena.petical.service.ClinicMedicineService;
import org.majimena.petical.web.utils.ErrorsUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;

/**
 * 動物病院医薬品コントローラ.
 */
@RestController
@RequestMapping("/api/v1/clinics/{clinicId}/medicines")
public class ClinicMedicineController {

    /**
     * 動物病院医薬品サービス.
     */
    @Inject
    private ClinicMedicineService clinicMedicineService;

    /**
     * 動物病院が使用できる医薬品を取得する.
     *
     * @param clinicId 動物病院ID
     * @return レスポンスエンティティ（正常時は200, 権限エラーは401, 異常時は500）
     */
    @Timed
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Medicine>> getAll(@PathVariable String clinicId) {
        // 動物病院の権限チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // 使用可能な医薬品を取得する
        List<Medicine> medicines = clinicMedicineService.getMedicinesByClinicId(clinicId);
        return ResponseEntity.ok().body(medicines);
    }
}
