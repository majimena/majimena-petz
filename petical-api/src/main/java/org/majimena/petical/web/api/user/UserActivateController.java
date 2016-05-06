package org.majimena.petical.web.api.user;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petical.domain.User;
import org.majimena.petical.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

/**
 * ユーザーをアクティベートするコントローラ.
 */
@RestController
@RequestMapping("/api/v1/activate")
public class UserActivateController {

    @Inject
    private UserService userService;

    @Timed
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<User> activateAccount(@RequestParam(value = "key") String key) {
        return userService.activateRegistration(key)
                .map(user -> ResponseEntity.ok().body(user))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
