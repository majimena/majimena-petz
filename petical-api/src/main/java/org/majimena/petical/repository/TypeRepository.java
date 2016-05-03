package org.majimena.petical.repository;

import org.majimena.petical.domain.Type;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 種別リポジトリ.
 */
public interface TypeRepository extends JpaRepository<Type, String> {

}
