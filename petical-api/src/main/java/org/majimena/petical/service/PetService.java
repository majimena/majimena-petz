package org.majimena.petical.service;

import org.majimena.petical.domain.Pet;
import org.majimena.petical.domain.pet.PetCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * ペットサービス.
 */
public interface PetService {

    /**
     * ユーザーをもとに、所有するペットを取得する.
     *
     * @param userId ユーザーID
     * @return ペット
     */
    List<Pet> getPetsByUserId(String userId);

    /**
     * 飼い主をもとに、所有するペットを取得する.
     *
     * @param customerId 飼い主ID
     * @return ペット
     */
    List<Pet> getPetsByCustomerId(String customerId);

    @Deprecated
    Page<Pet> getPetsByPetCriteria(PetCriteria criteria, Pageable pageable);

    Pet findPetByPetId(String id);

    Pet savePet(Pet pet);

    Pet uploadImage(String userId, String petId, byte[] binary);

}
