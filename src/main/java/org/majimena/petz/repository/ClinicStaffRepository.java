package org.majimena.petz.repository;

import org.majimena.petz.domain.ClinicStaff;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * クリニックスタッフリポジトリ.
 */
public interface ClinicStaffRepository extends JpaRepository<ClinicStaff, Long> {

}
