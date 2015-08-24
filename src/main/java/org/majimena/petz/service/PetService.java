package org.majimena.petz.service;

import org.majimena.petz.domain.Pet;

import java.util.List;

/**
 * Created by todoken on 2015/07/27.
 */
public interface PetService {

    List<Pet> findPetsByUserId(String userId);

    Pet findPetByPetId(String id);

    Pet savePet(Pet pet);

    Pet uploadImage(String userId, String petId, byte[] binary);

}
