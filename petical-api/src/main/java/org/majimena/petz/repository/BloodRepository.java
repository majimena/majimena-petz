package org.majimena.petz.repository;

import org.majimena.petz.domain.Blood;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 血液型リポジトリ.
 */
public interface BloodRepository extends JpaRepository<Blood, String> {

}
