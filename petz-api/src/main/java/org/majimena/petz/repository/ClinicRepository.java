package org.majimena.petz.repository;

import org.majimena.petz.domain.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * クリニックリポジトリ.
 */
public interface ClinicRepository extends JpaRepository<Clinic, String>, JpaSpecificationExecutor<Clinic> {

}
