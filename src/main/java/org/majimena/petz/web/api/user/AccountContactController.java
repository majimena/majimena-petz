package org.majimena.petz.web.api.user;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petz.domain.UserContact;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.UserService;
import org.springframework.http.ResponseEntity;
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
public class AccountContactController {

    @Inject
    private UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Timed
    @RequestMapping(value = "/account/contact", method = RequestMethod.GET)
    public ResponseEntity<UserContact> get() {
        String userId = SecurityUtils.getCurrentUserId();
        Optional<UserContact> contact = userService.getUserContactByUserId(userId);
        return contact
            .map(c -> ResponseEntity.ok(c))
            .orElse(ResponseEntity.ok().body(new UserContact()));
    }

    @Timed
    @RequestMapping(value = "/account/contact", method = RequestMethod.POST)
    public ResponseEntity<?> create(@RequestBody @Valid UserContact registry) {
        String userId = SecurityUtils.getCurrentUserId();
        registry.setId(userId);
        userService.saveUserContact(registry);
        return ResponseEntity.created(URI.create("/api/v1/account/contact")).build();
    }
}
