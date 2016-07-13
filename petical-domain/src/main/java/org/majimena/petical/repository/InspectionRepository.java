package org.majimena.petical.repository;

import org.majimena.petical.domain.Inspection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 診察料金リポジトリ.
 */
public interface InspectionRepository extends JpaRepository<Inspection, String>, JpaSpecificationExecutor<Inspection> {

}
