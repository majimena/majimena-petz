package org.majimena.petical.service.impl;

import org.majimena.petical.domain.Diagnosis;
import org.majimena.petical.repository.DiagnosisRepository;
import org.majimena.petical.service.DiagnosisService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

/**
 * 診断結果サービスの実装.
 */
@Service
@Transactional
public class DiagnosisServiceImpl implements DiagnosisService {
    /**
     * 診断結果リポジトリ.
     */
    @Inject
    private DiagnosisRepository diagnosisRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Diagnosis> getDiagnosises() {
        return diagnosisRepository.findAll();
    }
}
