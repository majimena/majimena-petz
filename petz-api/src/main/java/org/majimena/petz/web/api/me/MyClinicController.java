package org.majimena.petz.web.api.me;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.clinic.ClinicCriteria;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.ClinicService;
import org.majimena.petz.web.rest.util.PaginationUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.net.URISyntaxException;
import java.util.List;

/**
 * マイクリニックのコントローラ.
 */
@RestController
@RequestMapping("/api/v1")
public class MyClinicController {

    /**
     * クリニックサービス.
     */
    @Inject
    private ClinicService clinicService;

    /**
     * マイクリニックを取得する.
     *
     * @param offset ページング時のオフセット値
     * @param limit  １ページのサイズ
     * @return レスポンスエンティティ（通常時は200）
     * @throws URISyntaxException 通常発生しない例外
     */
    @Timed
    @RequestMapping(value = "/me/clinics", method = RequestMethod.GET)
    public ResponseEntity<List<Clinic>> getAll(@RequestParam(value = "page", required = false) Integer offset,
                                               @RequestParam(value = "per_page", required = false) Integer limit) throws URISyntaxException {
        // 検索条件を生成
        ClinicCriteria criteria = new ClinicCriteria();
        criteria.setUserId(SecurityUtils.getCurrentUserId());

        // ページング要素を加えて検索結果を返す
        Pageable pageable = PaginationUtil.generatePageRequest(offset, limit);
        Page<Clinic> page = clinicService.findMyClinicsByClinicCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/v1/me/clinics", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
}
