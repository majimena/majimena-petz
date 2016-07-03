package org.majimena.petical.repository;

import org.majimena.petical.domain.NvalItemComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 動物用医薬品アイテム成分リポジトリ.
 */
public interface NvalItemComponentRepository extends JpaRepository<NvalItemComponent, String>, JpaSpecificationExecutor<NvalItemComponent> {
}
