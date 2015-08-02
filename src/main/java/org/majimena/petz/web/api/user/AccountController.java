package org.majimena.petz.web.api.user;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petz.common.exceptions.ResourceNotFoundException;
import org.majimena.petz.domain.User;
import org.majimena.petz.domain.user.PasswordRegistry;
import org.majimena.petz.domain.user.SignupRegistry;
import org.majimena.petz.domain.user.UserPatchRegistry;
import org.majimena.petz.repository.UserRepository;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.MailService;
import org.majimena.petz.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

/**
 * Created by todoken on 2015/08/02.
 */
@RestController
@RequestMapping("/api/v1")
public class AccountController {

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserService userService;

    @Inject
    private MailService mailService;

    @Inject
    private SignupRegistryValidator signupRegistryValidator;

    @Inject
    private UserPatchRegistryValidator userPatchRegistryValidator;

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    public void setSignupRegistryValidator(SignupRegistryValidator signupRegistryValidator) {
        this.signupRegistryValidator = signupRegistryValidator;
    }

    public void setUserPatchRegistryValidator(UserPatchRegistryValidator userPatchRegistryValidator) {
        this.userPatchRegistryValidator = userPatchRegistryValidator;
    }

    @Timed
    @RequestMapping(value = "/account", method = RequestMethod.GET)
    public ResponseEntity<User> get() {
        String userId = SecurityUtils.getCurrentUserId();
        Optional<User> user = userService.getUserByUserId(userId);
        return user.map(u -> ResponseEntity.ok(u))
            .orElseThrow(() -> new ResourceNotFoundException("login user is not found."));
    }

    @Timed
    @RequestMapping(value = "/account", method = RequestMethod.PATCH)
    public ResponseEntity<User> patch(@RequestBody @Valid UserPatchRegistry registry) {
        String userId = SecurityUtils.getCurrentUserId();
        registry.setUserId(userId);
        User user = userService.updateUser(registry);
        return ResponseEntity.ok().body(user);
    }

    @Timed
    @RequestMapping(value = "/account", method = RequestMethod.POST)
    public ResponseEntity<?> create(@RequestBody @Valid SignupRegistry registry, BindingResult errors) throws BindException {
        signupRegistryValidator.validate(registry, errors);
        if (errors.hasErrors()) {
            throw new BindException(errors);
        }

        userService.saveUser(registry);
//        User user = userService.createUserInformation(registry.getLogin(), registry.getPassword(),
//            registry.getFirstName(), registry.getLastName(), registry.getEmail().toLowerCase(),
//            registry.getLangKey());
//        mailService.sendActivationEmail(user, "");
        return ResponseEntity.created(URI.create("/api/v1/account")).build();
    }

    @Timed
    @RequestMapping(value = "/account/password", method = RequestMethod.PUT)
    public ResponseEntity<Void> changePassword(@RequestBody @Valid PasswordRegistry registry) {
        String userId = SecurityUtils.getCurrentUserId();
        registry.setUserId(userId);
        userService.changePassword(registry);
        return ResponseEntity.ok().build();
    }

    @Timed
    @RequestMapping(value = "/account/password", method = RequestMethod.DELETE)
    public ResponseEntity<?> requestPasswordReset(@RequestBody String mail, HttpServletRequest request) {
        return userService.requestPasswordReset(mail)
            .map(user -> {
                String baseUrl = request.getScheme() +
                    "://" +
                    request.getServerName() +
                    ":" +
                    request.getServerPort();
                mailService.sendPasswordResetMail(user, baseUrl);
                return new ResponseEntity<>("e-mail was sent", HttpStatus.OK);
            }).orElse(new ResponseEntity<>("e-mail address not registered", HttpStatus.BAD_REQUEST));

    }

    @Timed
    @RequestMapping(value = "/account/password", method = RequestMethod.POST)
    public ResponseEntity<String> finishPasswordReset(@RequestParam(value = "key") String key, @RequestParam(value = "newPassword") String newPassword) {
        return userService.completePasswordReset(newPassword, key)
            .map(user -> new ResponseEntity<String>(HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
