package org.majimena.petz.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petz.domain.User;
import org.majimena.petz.repository.UserRepository;
import org.majimena.petz.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

/**
 * REST controller for managing users.
 */
@RestController
@RequestMapping("/api")
public class UserResource {

    @Inject
    protected UserRepository userRepository;

    @Inject
    protected UserService userService;

    @Timed
    @RequestMapping(value = "/users/me", method = RequestMethod.GET)
    public ResponseEntity<User> getMe() {
        User user = userService.getUserWithAuthorities();
        return ResponseEntity.ok().body(user);
    }

    @Timed
    @RequestMapping(value = "/users/{login}", method = RequestMethod.GET)
    public ResponseEntity<User> getUser(@PathVariable String login) {
        // TODO 見せていいの？
        return userRepository.findOneByLogin(login)
            .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
