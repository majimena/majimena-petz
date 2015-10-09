package org.majimena.petz.web.api.pet;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petz.repository.TypeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 種別コントローラ.
 */
@RestController
@RequestMapping("/api/v1")
public class TypeController {

    @Inject
    private TypeRepository typeRepository;

    public void setTypeRepository(TypeRepository typeRepository) {
        this.typeRepository = typeRepository;
    }

    @Timed
    @RequestMapping(value = "/types", method = RequestMethod.GET)
    public ResponseEntity<List<String>> getAll() {
        List<String> list = typeRepository.findAll().stream()
            .map(t -> t.getName()).collect(Collectors.toList());
        return ResponseEntity.ok().body(list);
    }
}
