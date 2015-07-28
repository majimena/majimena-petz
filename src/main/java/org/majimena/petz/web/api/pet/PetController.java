package org.majimena.petz.web.api.pet;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petz.domain.Pet;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.PetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
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

    public void setPetService(PetService petService) {
        this.petService = petService;
    }

    @Timed
    @RequestMapping(value = "/pets", method = RequestMethod.GET)
    public ResponseEntity<List<Pet>> get() {
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
}
