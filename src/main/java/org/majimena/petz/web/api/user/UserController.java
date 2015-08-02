package org.majimena.petz.web.api.user;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petz.domain.User;
import org.majimena.petz.repository.UserRepository;
import org.majimena.petz.service.MailService;
import org.majimena.petz.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Optional;

/**
 * REST controller for managing users.
 */
@RestController
@RequestMapping("/api/v1")
public class UserController {

    @Inject
    protected UserRepository userRepository;

    @Inject
    protected UserService userService;

    @Inject
    protected MailService mailService;

    @Inject
    protected SignupRegistryValidator signupRegistryValidator;

    // TODO 見せていいの？
    @Timed
    @RequestMapping(value = "/users/{login}", method = RequestMethod.GET)
    public ResponseEntity<User> get(@PathVariable String login) {
        return userRepository.findOneByLogin(login)
            .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * GET  /activate -> activate the registered user.
     */
    @Timed
    @RequestMapping(value = "/activate", method = RequestMethod.GET)
    public ResponseEntity<String> activateAccount(@RequestParam(value = "key") String key) {
        return Optional.ofNullable(userService.activateRegistration(key))
            .map(user -> new ResponseEntity<String>(HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
