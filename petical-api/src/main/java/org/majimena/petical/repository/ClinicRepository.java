package org.majimena.petical.repository;

import org.majimena.petical.domain.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * クリニックリポジトリ.
 */
public interface ClinicRepository extends JpaRepository<Clinic, String>, JpaSpecificationExecutor<Clinic> {
}
