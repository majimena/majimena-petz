package org.majimena.petz.web.api.type;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petz.domain.Type;
import org.majimena.petz.repository.TypeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;

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
    public ResponseEntity<List<Type>> getAll() {
        List<Type> list = typeRepository.findAll();
        return ResponseEntity.ok().body(list);
    }
}
