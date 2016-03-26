package org.majimena.petical.repository;

import org.majimena.petical.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * タグリポジトリ.
 */
public interface TagRepository extends JpaRepository<Tag, String> {

}
