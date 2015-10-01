package org.majimena.petz.web.api.clinic;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petz.common.exceptions.ResourceCannotAccessException;
import org.majimena.petz.domain.Pet;
import org.majimena.petz.domain.clinic.ClinicPetCriteria;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.PetService;
import org.majimena.petz.web.rest.util.PaginationUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URISyntaxException;
import java.util.List;

/**
 * クリニックの管理下にあるペットを操作するコントローラ.
 */
@RestController
@RequestMapping("/api/v1")
public class ClinicPetController {

    /**
     * ペットサービス.
     */
    @Inject
    private PetService petService;

    /**
     * ペットサービスを設定する.
     *
     * @param petService ペットサービス
     */
    public void setPetService(PetService petService) {
        this.petService = petService;
    }

    /**
     * 自分のクリニックのペットを検索する.
     *
     * @param clinicId クリニックID
     * @param offset   検索時のオフセット値
     * @param limit    検索結果数の上限値
     * @param criteria 検索条件
     * @return 検索結果
     * @throws URISyntaxException URIエラー
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/pets", method = RequestMethod.GET)
    public ResponseEntity<List<Pet>> getAll(@PathVariable String clinicId,
                                            @RequestParam(value = "page", required = false) Integer offset,
                                            @RequestParam(value = "per_page", required = false) Integer limit,
                                            @Valid ClinicPetCriteria criteria) throws URISyntaxException {
        // クリニックの権限チェック
        if (!SecurityUtils.isUserInClinic(clinicId)) {
            throw new ResourceCannotAccessException(); // FIXME メッセージ詰める
        }

        Pageable pageable = PaginationUtil.generatePageRequest(offset, limit);
        criteria.setClinicId(clinicId);

        // ペットを検索する
        Page<Pet> pets = petService.getPetsByClinicPetCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(pets, "/api/v1/clinics/" + clinicId + "/pets", offset, limit);
        return new ResponseEntity<>(pets.getContent(), headers, HttpStatus.OK);
    }
}
