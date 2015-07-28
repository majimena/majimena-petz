package org.majimena.petz.service.impl;

import org.majimena.petz.common.exceptions.ResourceNotFoundException;
import org.majimena.petz.domain.Pet;
import org.majimena.petz.domain.User;
import org.majimena.petz.repository.PetRepository;
import org.majimena.petz.repository.TagRepository;
import org.majimena.petz.repository.TypeRepository;
import org.majimena.petz.repository.UserRepository;
import org.majimena.petz.service.PetService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by todoken on 2015/07/27.
 */
@Service
@Transactional
public class PetServiceImpl implements PetService {

    @Inject
    private UserRepository userRepository;

    @Inject
    private PetRepository petRepository;

    @Inject
    private TypeRepository typeRepository;

    @Inject
    private TagRepository tagRepository;

    @Override
    public List<Pet> findPetsByUserId(String userId) {
        List<Pet> pets = petRepository.findByUserId(userId);
        pets.stream().forEach(pet -> {
            // lazy load
            pet.getTypes().size();
            pet.getTags().size();
        });
        return pets;
    }

    @Override
    public Pet findPetByPetId(String id) {
        // ペットを取得する
        Pet one = petRepository.findOne(id);
        if (one == null) {
            throw new ResourceNotFoundException("[" + id + "] is not found. check it yourself.");
        }

        // lazy load
        one.getTypes().size();
        one.getTags().size();

        return one;
    }

    @Override
    public Pet savePet(Pet pet) {
        // ペットの種別とタグを登録（既にあれば登録しない）
        pet.getTypes().stream()
            .forEach(type -> typeRepository.saveAndFlush(type));
        pet.getTags().stream()
            .forEach(tag -> tagRepository.saveAndFlush(tag));

        // 飼い主を親キーにしてペットを登録
        User user = userRepository.findOne(pet.getUser().getId());
        pet.setUser(user);
        return petRepository.saveAndFlush(pet);
    }
}
