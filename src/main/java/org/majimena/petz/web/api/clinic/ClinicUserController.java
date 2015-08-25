package org.majimena.petz.web.api.clinic;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petz.domain.User;
import org.majimena.petz.domain.clinic.ClinicInvitationRegistry;
import org.majimena.petz.domain.user.SignupRegistry;
import org.majimena.petz.domain.user.UserPatchRegistry;
import org.majimena.petz.service.UserService;
import org.majimena.petz.web.api.user.SignupRegistryValidator;
import org.majimena.petz.web.api.user.UserPatchRegistryValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class ClinicUserController {

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
    @RequestMapping(value = "/clinics/{clinicId}/users", method = RequestMethod.GET)
    public ResponseEntity<User> get(@PathVariable String clinicId) {
        // 自分のクリニックの顧客を取得
        return null;
    }

    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/users/{userId}", method = RequestMethod.PUT)
    public ResponseEntity<User> get(@PathVariable String clinicId, @PathVariable String userId) {
        // 自分のクリニックの顧客を取得（権限チェックが必要）
        Optional<User> user = userService.getUserByUserId(userId);
        return user
                .map(u -> ResponseEntity.ok(u))
                .orElse(new ResponseEntity<User>(HttpStatus.NOT_FOUND));
    }

    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/users", method = RequestMethod.POST)
    public ResponseEntity<Void> post(@PathVariable String clinicId, @PathVariable String userId, @RequestBody @Valid SignupRegistry registry, BindingResult errors) throws BindException {
        // TODO 権限チェックが必要
        signupRegistryValidator.validate(registry, errors);
        if (errors.hasErrors()) {
            throw new BindException(errors);
        }

        // TODO 自動でクリニックに紐付けする必要があると思われる
        userService.saveUser(registry);
        //        User user = userService.createUserInformation(registry.getLogin(), registry.getPassword(),
        //            registry.getFirstName(), registry.getLastName(), registry.getEmail().toLowerCase(),
        //            registry.getLangKey());
        //        mailService.sendActivationEmail(user, "");
        return ResponseEntity.created(URI.create("/api/v1/users")).build();
    }

    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/users/{userId}", method = RequestMethod.PATCH)
    public ResponseEntity<Void> patch(@PathVariable String clinicId, @PathVariable String userId, @RequestBody @Valid UserPatchRegistry registry) {
        // TODO 権限チェックが必要
        registry.setUserId(userId);
        userService.updateUser(registry);
        return ResponseEntity.ok().build();
    }

    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/users/{userId}/authorize", method = RequestMethod.POST)
    public ResponseEntity<Void> authorize(@PathVariable String clinicId, @Valid @RequestBody ClinicInvitationRegistry registry, BindingResult errors) throws BindException {
        // クリニックの顧客をアクティブな状態で追加
        // 電話番号か氏名で認証
        return null;
    }
}
