package org.majimena.petz.repository;

import org.majimena.petz.domain.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * ペットリポジトリ.
 */
public interface PetRepository extends JpaRepository<Pet, String>, JpaSpecificationExecutor<Pet> {

}
