package org.majimena.petz.service;

import org.majimena.petz.domain.Pet;
import org.majimena.petz.domain.clinic.ClinicPetCriteria;
import org.majimena.petz.domain.pet.PetCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * ペットサービス.
 */
public interface PetService {

    Page<Pet> getPetsByPetCriteria(PetCriteria criteria, Pageable pageable);

    /**
     * 指定したクリニックの管理化にあるペットを検索する.
     *
     * @param criteria クリニックペット検索条件
     * @param pageable ページング情報
     * @return 該当するペット
     */
    Page<Pet> getPetsByClinicPetCriteria(ClinicPetCriteria criteria, Pageable pageable);

    Pet findPetByPetId(String id);

    Pet savePet(Pet pet);

    Pet uploadImage(String userId, String petId, byte[] binary);

}
