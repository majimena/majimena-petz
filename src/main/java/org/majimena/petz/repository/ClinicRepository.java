package org.majimena.petz.repository;

import org.majimena.petz.domain.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * クリニックリポジトリ.
 */
public interface ClinicRepository extends JpaRepository<Clinic, Long> {

}
