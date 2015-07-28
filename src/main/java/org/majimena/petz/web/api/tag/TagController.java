package org.majimena.petz.web.api.tag;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petz.domain.Tag;
import org.majimena.petz.repository.TagRepository;
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
public class TagController {

    @Inject
    private TagRepository tagRepository;

    public void setTagRepository(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Timed
    @RequestMapping(value = "/tags", method = RequestMethod.GET)
    public ResponseEntity<List<Tag>> getAll() {
        List<Tag> list = tagRepository.findAll();
        return ResponseEntity.ok().body(list);
    }
}
