package org.majimena.petz.service.impl;

import org.majimena.framework.aws.AmazonS3Service;
import org.majimena.petz.common.exceptions.ResourceNotFoundException;
import org.majimena.petz.domain.*;
import org.majimena.petz.repository.*;
import org.majimena.petz.service.PetService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ペットサービスの実装.
 */
@Service
public class PetServiceImpl implements PetService {

    @Inject
    private UserRepository userRepository;

    @Inject
    private PetRepository petRepository;

    @Inject
    private TypeRepository typeRepository;

    @Inject
    private ColorRepository colorRepository;

    @Inject
    private BloodRepository bloodRepository;

    @Inject
    private TagRepository tagRepository;

    @Inject
    private AmazonS3Service amazonS3Service;

    @Override
    @Transactional(readOnly = true)
    public List<Pet> findPetsByUserId(String userId) {
        List<Pet> pets = petRepository.findByUserId(userId);
        return pets;
    }

    @Override
    @Transactional(readOnly = true)
    public Pet findPetByPetId(String id) {
        // ペットを取得する
        Pet pet = petRepository.findOne(id);
        if (pet == null) {
            throw new ResourceNotFoundException("Pet Entity [" + id + "] is not found. check it yourself.");
        }
        return pet;
    }

    @Override
    @Transactional
    public Pet savePet(Pet pet) {
        // ペットの種別、毛色、血液型を登録してペットと紐付けする（既にあれば登録しない）
        Type type = typeRepository.save(pet.getType());
        Color color = colorRepository.save(pet.getColor());
        Blood blood = null;
        if (pet.getBlood() != null) {
            blood = bloodRepository.save(pet.getBlood());
        }

        // ペットのタグを登録してペットと紐付けする（既にあれば登録しない）
        Set<Tag> tags = null;
        if (pet.getTags() != null) {
            tags = pet.getTags().stream()
                .map(tag -> tagRepository.save(tag)).collect(Collectors.toSet());
        }

        // 飼い主を親キーにしてペットを登録
        User user = userRepository.findOne(pet.getUser().getId());
        pet.setUser(user);
        pet.setType(type);
        pet.setColor(color);
        pet.setBlood(blood);
        pet.setTags(tags);
        return petRepository.save(pet);
    }

    @Override
    @Transactional
    public Pet uploadImage(String userId, String petId, byte[] binary) {
        // 先にS3にファイルを保存
        String filename = "pets/" + petId + "/profile/" + System.currentTimeMillis() + ".jpg";
        String url = amazonS3Service.upload(filename, binary);

        // 該当ペットのイメージとしてURLを紐付けする
        Pet pet = petRepository.findOne(petId);
        pet.setImage(url);
        petRepository.save(pet);

        return findPetByPetId(petId);
    }
}
