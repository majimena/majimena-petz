package org.majimena.petical.web.api.pet;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petical.repository.KindRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 種類コントローラ.
 */
@RestController
@RequestMapping("/api/v1")
public class KindController {

    @Inject
    private KindRepository kindRepository;

    @Timed
    @RequestMapping(value = "/kinds", method = RequestMethod.GET)
    public ResponseEntity<List<String>> getAll() {
        List<String> list = kindRepository.findAll().stream()
                .map(t -> t.getName())
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(list);
    }
}
