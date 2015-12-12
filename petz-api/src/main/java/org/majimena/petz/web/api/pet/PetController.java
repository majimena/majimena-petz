package org.majimena.petz.web.api.pet;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petz.common.aws.AmazonS3Service;
import org.majimena.petz.domain.Pet;
import org.majimena.petz.domain.pet.PetCriteria;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.PetService;
import org.majimena.petz.web.rest.util.PaginationUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
    public ResponseEntity<List<Pet>> getAll(@RequestParam(value = "page", required = false) Integer offset,
                                            @RequestParam(value = "per_page", required = false) Integer limit,
                                            @Valid PetCriteria criteria) throws URISyntaxException {
        Pageable pageable = PaginationUtil.generatePageRequest(offset, limit);
        Page<Pet> pets = petService.getPetsByPetCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(pets, "/api/v1/pets", offset, limit);
        return new ResponseEntity<>(pets.getContent(), headers, HttpStatus.OK);
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
