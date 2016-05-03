package org.majimena.petical.repository;

import org.majimena.petical.domain.Color;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 毛色リポジトリ.
 */
public interface ColorRepository extends JpaRepository<Color, String> {

}
