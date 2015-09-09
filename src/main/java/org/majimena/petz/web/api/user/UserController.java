package org.majimena.petz.web.api.user;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petz.domain.user.UserCriteria;
import org.majimena.petz.domain.user.UserOutline;
import org.majimena.petz.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;
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

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setSignupRegistryValidator(SignupRegistryValidator signupRegistryValidator) {
        this.signupRegistryValidator = signupRegistryValidator;
    }

    @Timed
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ResponseEntity<List<UserOutline>> get(@Valid UserCriteria criteria) {
        // パブリックになってもあまり問題のない情報だけ返す（但し、アクティベートされていないユーザは返さない）
        List<UserOutline> users = userService.getUsersByUserCriteria(criteria);
        return ResponseEntity.ok().body(users);
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
