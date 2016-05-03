package org.majimena.petical.repository;

import org.majimena.petical.domain.Vaccine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * ワクチンリポジトリ.
 */
public interface VaccineRepository
        extends JpaRepository<Vaccine, String>, JpaSpecificationExecutor<Vaccine> {

}
