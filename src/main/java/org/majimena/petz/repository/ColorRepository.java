package org.majimena.petz.repository;

import org.majimena.petz.domain.Color;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 毛色リポジトリ.
 */
public interface ColorRepository extends JpaRepository<Color, String> {

}
