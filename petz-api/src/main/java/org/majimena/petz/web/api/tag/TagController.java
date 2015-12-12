package org.majimena.petz.web.api.tag;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petz.repository.TagRepository;
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
public class TagController {

    @Inject
    private TagRepository tagRepository;

    public void setTagRepository(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Timed
    @RequestMapping(value = "/tags", method = RequestMethod.GET)
    public ResponseEntity<List<String>> getAll() {
        List<String> list = tagRepository.findAll().stream()
                .map(t -> t.getName()).collect(Collectors.toList());
        return ResponseEntity.ok().body(list);
    }
}
