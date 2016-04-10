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

    List<Pet> getPetsByUserId(String userId);

    Page<Pet> getPetsByPetCriteria(PetCriteria criteria, Pageable pageable);

    Pet findPetByPetId(String id);

    Pet savePet(Pet pet);

    Pet uploadImage(String userId, String petId, byte[] binary);

}