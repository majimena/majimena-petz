package org.majimena.petz.repository;

import org.majimena.petz.domain.Chart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * カルテリポジトリ.
 */
public interface ChartRepository extends JpaRepository<Chart, String>, JpaSpecificationExecutor<Chart> {

}
