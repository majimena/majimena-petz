package org.majimena.petical.repository;

import org.majimena.petical.domain.NvalItem;
import org.majimena.petical.domain.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 動物用医薬品アイテムリポジトリ.
 */
public interface NvalItemRepository extends JpaRepository<NvalItem, String>, JpaSpecificationExecutor<NvalItem> {
}
