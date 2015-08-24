package org.majimena.petz.web.api.pet;

import com.codahale.metrics.annotation.Timed;
import org.majimena.framework.aws.AmazonS3Service;
import org.majimena.petz.domain.Pet;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.PetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.util.List;

/**
 * ペットコントローラ.
 */
@RestController
@RequestMapping("/api/v1")
public class PetController {

    @Inject
    private PetService petService;

    @Inject
    private AmazonS3Service amazonWebService;

    public void setPetService(PetService petService) {
        this.petService = petService;
    }

    @Timed
    @RequestMapping(value = "/pets", method = RequestMethod.GET)
    public ResponseEntity<List<Pet>> get() {
        // FIXME 検索されていないときはログインユーザのペット一覧
        String userId = SecurityUtils.getCurrentUserId();
        List<Pet> list = petService.findPetsByUserId(userId);
        return ResponseEntity.ok().body(list);
    }

    @Timed
    @RequestMapping(value = "/pets/{id}", method = RequestMethod.GET)
    public ResponseEntity<Pet> show(@PathVariable String id) {
        Pet pet = petService.findPetByPetId(id);
        return ResponseEntity.ok().body(pet);
    }

    @Timed
    @RequestMapping(value = "/pets", method = RequestMethod.POST)
    public ResponseEntity<Pet> post(@Valid @RequestBody Pet pet) {
        pet.getUser().setId(SecurityUtils.getCurrentUserId());
        Pet save = petService.savePet(pet);
        return ResponseEntity.created(URI.create("/api/v1/pets")).body(save);
    }

    @Timed
    @RequestMapping(value = "/pets/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Pet> put(@PathVariable String id, @Valid @RequestBody Pet pet) {
        // FIXME ログインユーザーのペットだけ更新可能
        // TODO add validator
        pet.setId(id);
        pet.getUser().setId(SecurityUtils.getCurrentUserId());
        Pet save = petService.savePet(pet);
        return ResponseEntity.ok().body(save);
    }

    @Timed
    @RequestMapping(value = "/pets/{id}/images", method = RequestMethod.POST)
    public ResponseEntity<Pet> upload(@PathVariable String id, @RequestParam("file") MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            String userId = SecurityUtils.getCurrentUserId();
            Pet pet = petService.uploadImage(userId, id, file.getBytes());
            return ResponseEntity.ok().body(pet);
        }
        return ResponseEntity.unprocessableEntity().body(null);
    }
}
