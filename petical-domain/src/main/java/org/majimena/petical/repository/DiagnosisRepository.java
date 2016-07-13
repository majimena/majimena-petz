package org.majimena.petical.repository;

import org.majimena.petical.domain.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 診断結果マスタのリポジトリ.
 */
public interface DiagnosisRepository extends JpaRepository<Diagnosis, String>, JpaSpecificationExecutor<Diagnosis> {
}
