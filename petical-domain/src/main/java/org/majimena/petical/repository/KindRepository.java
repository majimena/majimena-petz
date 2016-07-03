package org.majimena.petical.repository;

import org.majimena.petical.domain.Kind;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 種類リポジトリ.
 */
public interface KindRepository extends JpaRepository<Kind, String> {

}
