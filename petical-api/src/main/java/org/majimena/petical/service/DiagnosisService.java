package org.majimena.petical.service;

import org.majimena.petical.domain.Diagnosis;

import java.util.List;

/**
 * 診断結果サービス.
 */
public interface DiagnosisService {

    /**
     * 全ての診断結果情報を取得する.
     *
     * @return 診断結果
     */
    List<Diagnosis> getDiagnosises();

}
