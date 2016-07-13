package org.majimena.petical.repository;

import org.majimena.petical.domain.Blood;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 血液型リポジトリ.
 */
public interface BloodRepository extends JpaRepository<Blood, String> {

}
