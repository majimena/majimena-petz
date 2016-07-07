package org.majimena.petical.web.api.diagnosises;

import com.codahale.metrics.annotation.Timed;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;
import org.majimena.petical.domain.Diagnosis;
import org.majimena.petical.service.DiagnosisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 診断結果コントローラ.
 */
@RestController
@RequestMapping("/api/v1/diagnosises")
public class DiagnosisController {

    /**
     * 診断結果サービス.
     */
    @Inject
    private DiagnosisService diagnosisService;

    /**
     * 診断結果を取得する.
     *
     * @return レスポンスエンティティ（正常時は200, 異常時は500）
     */
    @Timed
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Diagnosis>> getAll() {
        List<Diagnosis> diagnosises = diagnosisService.getDiagnosises();
        return ResponseEntity.ok().body(diagnosises);
    }
}
