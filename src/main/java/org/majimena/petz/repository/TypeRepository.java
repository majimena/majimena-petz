package org.majimena.petz.repository;

import org.majimena.petz.domain.Type;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 種別リポジトリ.
 */
public interface TypeRepository extends JpaRepository<Type, String> {

}
