package org.majimena.petical.web.api.clinics.customers;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petical.domain.Pet;
import org.majimena.petical.security.SecurityUtils;
import org.majimena.petical.service.PetService;
import org.majimena.petical.web.utils.ErrorsUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;

/**
 * 飼い主のペットのコントローラ.
 */
@RestController
@RequestMapping("/api/v1/clinics/{clinicId}/customers/{customerId}/pets")
public class ClinicCustomerPetController {

    /**
     * 顧客サービス.
     */
    @Inject
    private PetService petService;

    /**
     * クリニックで管理しているこの顧客のカルテを取得する.
     *
     * @param clinicId   クリニックID
     * @param customerId 顧客ID
     * @return レスポンスエンティティ（通常時は200）
     */
    @Timed
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Pet>> get(@PathVariable String clinicId, @PathVariable String customerId) {
        // クリニックの権限チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        ErrorsUtils.throwIfNotIdentify(customerId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // 参照可能な顧客であれば、その顧客のカルテを全て取得する
        List<Pet> charts = petService.getPetsByCustomerId(customerId);
        // FIXME 余計なレスポンスは返さないようにする
        return ResponseEntity.ok().body(charts);
    }
}
