package org.majimena.petz.repository;

import org.majimena.petz.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * タグリポジトリ.
 */
public interface TagRepository extends JpaRepository<Tag, String> {

}
