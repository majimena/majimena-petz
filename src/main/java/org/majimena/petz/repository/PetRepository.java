package org.majimena.petz.repository;

import org.majimena.petz.domain.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * ペットリポジトリ.
 */
public interface PetRepository extends JpaRepository<Pet, String> {

    List<Pet> findByUserId(String userId);

}
