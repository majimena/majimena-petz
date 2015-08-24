package org.majimena.petz.web.api.user;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petz.domain.UserContact;
import org.majimena.petz.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

/**
 * Created by todoken on 2015/08/02.
 */
@RestController
@RequestMapping("/api/v1")
public class UserContactController {

    @Inject
    private UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Timed
    @RequestMapping(value = "/users/{userId}/contact", method = RequestMethod.GET)
    public ResponseEntity<UserContact> get(@PathVariable String userId) {
        // TODO 権限チェックが必要
        Optional<UserContact> contact = userService.getUserContactByUserId(userId);
        return contact
                .map(c -> ResponseEntity.ok(c))
                .orElse(ResponseEntity.ok().body(new UserContact()));
    }

    @Timed
    @RequestMapping(value = "/users/{userId}/contact", method = RequestMethod.POST)
    public ResponseEntity<?> create(@PathVariable String userId, @RequestBody @Valid UserContact registry) {
        // TODO 権限チェックが必要
        registry.setId(userId);
        userService.saveUserContact(registry);
        return ResponseEntity.created(URI.create("/api/v1/users/" + userId + "/contact")).build();
    }
}
