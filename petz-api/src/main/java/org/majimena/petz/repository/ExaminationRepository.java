package org.majimena.petz.repository;

import org.majimena.petz.domain.Examination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 診察リポジトリ.
 */
public interface ExaminationRepository
        extends JpaRepository<Examination, String>, JpaSpecificationExecutor<Examination> {

}
