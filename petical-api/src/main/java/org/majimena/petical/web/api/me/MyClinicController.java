package org.majimena.petical.web.api.me;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petical.domain.Clinic;
import org.majimena.petical.security.SecurityUtils;
import org.majimena.petical.service.ClinicService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
     * @return レスポンスエンティティ（通常時は200）
     * @throws URISyntaxException 通常発生しない例外
     */
    @Timed
    @RequestMapping(value = "/me/clinics", method = RequestMethod.GET)
    public ResponseEntity<List<Clinic>> get() throws URISyntaxException {
        String userId = SecurityUtils.getCurrentUserId();
        List<Clinic> list = clinicService.getMyClinicsByUserId(userId);
        return ResponseEntity.ok().body(list);
    }
}
