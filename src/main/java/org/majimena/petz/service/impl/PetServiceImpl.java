package org.majimena.petz.service.impl;

import org.majimena.petz.common.exceptions.ResourceNotFoundException;
import org.majimena.petz.domain.Pet;
import org.majimena.petz.domain.Tag;
import org.majimena.petz.domain.Type;
import org.majimena.petz.domain.User;
import org.majimena.petz.repository.PetRepository;
import org.majimena.petz.repository.TagRepository;
import org.majimena.petz.repository.TypeRepository;
import org.majimena.petz.repository.UserRepository;
import org.majimena.petz.service.PetService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
            // lazy load (many to many entities -> string set)
            Stream<String> types = pet.getTypeEntities().stream().map(t -> t.getName());
            pet.setTypes(types.collect(Collectors.toSet()));
            Stream<String> tags = pet.getTagEntities().stream().map(t -> t.getName());
            pet.setTags(tags.collect(Collectors.toSet()));
        });
        return pets;
    }

    @Override
    public Pet findPetByPetId(String id) {
        // ペットを取得する
        Pet pet = petRepository.findOne(id);
        if (pet == null) {
            throw new ResourceNotFoundException("[" + id + "] is not found. check it yourself.");
        }

        // lazy load (many to many entities -> string set)
        Stream<String> types = pet.getTypeEntities().stream().map(t -> t.getName());
        pet.setTypes(types.collect(Collectors.toSet()));
        Stream<String> tags = pet.getTagEntities().stream().map(t -> t.getName());
        pet.setTags(tags.collect(Collectors.toSet()));

        return pet;
    }

    @Override
    public Pet savePet(Pet pet) {
        // ペットの種別を登録してペットと紐付けする（既にあれば登録しない）
        pet.setTypeEntities(new HashSet<>());
        pet.getTypes().stream().forEach(type -> {
            Type save = typeRepository.saveAndFlush(new Type(type));
            pet.getTypeEntities().add(save);
        });

        // ペットのタグを登録してペットと紐付けする（既にあれば登録しない）
        if (pet.getTags() != null) {
            pet.setTagEntities(new HashSet<>());
            pet.getTags().stream().forEach(tag -> {
                Tag save = tagRepository.saveAndFlush(new Tag(tag));
                pet.getTagEntities().add(save);
            });
        }

        // 飼い主を親キーにしてペットを登録
        User user = userRepository.findOne(pet.getUser().getId());
        pet.setUser(user);
        return petRepository.save(pet);
    }
}
