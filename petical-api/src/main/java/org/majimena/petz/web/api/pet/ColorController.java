package org.majimena.petz.web.api.pet;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petz.repository.ColorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 毛色コントローラ.
 */
@RestController
@RequestMapping("/api/v1")
public class ColorController {

    @Inject
    private ColorRepository colorRepository;

    public void setColorRepository(ColorRepository colorRepository) {
        this.colorRepository = colorRepository;
    }

    @Timed
    @RequestMapping(value = "/colors", method = RequestMethod.GET)
    public ResponseEntity<List<String>> getAll() {
        List<String> list = colorRepository.findAll().stream()
                .map(t -> t.getName()).collect(Collectors.toList());
        return ResponseEntity.ok().body(list);
    }
}
