package org.majimena.petz.web.api.user;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petz.domain.User;
import org.majimena.petz.domain.user.SignupRegistry;
import org.majimena.petz.repository.UserRepository;
import org.majimena.petz.service.MailService;
import org.majimena.petz.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
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

    @Timed
    @RequestMapping(value = "/users/me", method = RequestMethod.GET)
    public ResponseEntity<User> get() {
        User user = userService.getUserWithAuthorities();
        return ResponseEntity.ok().body(user);
    }

    // TODO 見せていいの？
    @Timed
    @RequestMapping(value = "/users/{login}", method = RequestMethod.GET)
    public ResponseEntity<User> get(@PathVariable String login) {
        return userRepository.findOneByLogin(login)
            .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Timed
    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public ResponseEntity<?> create(@Valid @RequestBody SignupRegistry registry, BindingResult errors) throws BindException {
        signupRegistryValidator.validate(registry, errors);
        if (errors.hasErrors()) {
            throw new BindException(errors);
        }

        userService.saveUser(registry);
//        User user = userService.createUserInformation(registry.getLogin(), registry.getPassword(),
//            registry.getFirstName(), registry.getLastName(), registry.getEmail().toLowerCase(),
//            registry.getLangKey());
//        mailService.sendActivationEmail(user, "");
        return ResponseEntity.created(URI.create("/api/v1/users")).build();
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
