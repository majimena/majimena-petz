package org.majimena.petical.repository;

import org.majimena.petical.domain.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 共通医薬品マスタのリポジトリ.
 */
public interface MedicineRepository extends JpaRepository<Medicine, String>, JpaSpecificationExecutor<Medicine> {
}
