package org.majimena.petz.repository;

import org.majimena.petz.domain.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * ペットリポジトリ.
 */
public interface PetRepository extends JpaRepository<Pet, String>, JpaSpecificationExecutor<Pet> {

    /**
     * ユーザーIDをもとに、全てのペットを取得する.
     *
     * @param userId ユーザーID
     * @return ペット一覧
     */
    List<Pet> findByUserId(String userId);
}
