package org.majimena.petz.web.api.user;

import com.codahale.metrics.annotation.Timed;
import org.majimena.framework.beans.factory.BeanFactory;
import org.majimena.petz.domain.User;
import org.majimena.petz.domain.user.SignupRegistry;
import org.majimena.petz.domain.user.UserCriteria;
import org.majimena.petz.domain.user.UserOutline;
import org.majimena.petz.domain.user.UserPatchRegistry;
import org.majimena.petz.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing users.
 */
@RestController
@RequestMapping("/api/v1")
public class UserController {

    @Inject
    private UserService userService;

    @Inject
    private SignupRegistryValidator signupRegistryValidator;

    @Inject
    private UserPatchRegistryValidator userPatchRegistryValidator;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setSignupRegistryValidator(SignupRegistryValidator signupRegistryValidator) {
        this.signupRegistryValidator = signupRegistryValidator;
    }

    public void setUserPatchRegistryValidator(UserPatchRegistryValidator userPatchRegistryValidator) {
        this.userPatchRegistryValidator = userPatchRegistryValidator;
    }

    @Timed
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ResponseEntity<List<UserOutline>> get(@Valid UserCriteria criteria) {
        // パブリックになってもあまり問題のない情報だけ返す
        List<UserOutline> users = userService.getUsersByUserCriteria(criteria);
        return ResponseEntity.ok().body(users);
    }

    @Timed
    @RequestMapping(value = "/users/{userId}", method = RequestMethod.GET)
    public ResponseEntity<UserOutline> get(@PathVariable String userId) {
        // パブリックになってもあまり問題のない情報だけ返す
        Optional<User> user = userService.getUserByUserId(userId);
        return user
                .map(u -> ResponseEntity.ok(BeanFactory.create(u, new UserOutline())))
                .orElse(new ResponseEntity<UserOutline>(HttpStatus.NOT_FOUND));
    }

    @Timed
    @RequestMapping(value = "/users/{userId}", method = RequestMethod.PATCH)
    public ResponseEntity<Void> patch(@PathVariable String userId, @RequestBody @Valid UserPatchRegistry registry) {
        // TODO 権限チェックが必要
        registry.setUserId(userId);
        userService.updateUser(registry);
        return ResponseEntity.ok().build();
    }

    @Timed
    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public ResponseEntity<Void> post(@PathVariable String userId, @RequestBody @Valid SignupRegistry registry, BindingResult errors) throws BindException {
        // TODO 権限チェックが必要
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

    // ---------- 以下はオリジナル

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
