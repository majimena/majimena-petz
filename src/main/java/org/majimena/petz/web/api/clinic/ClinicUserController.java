package org.majimena.petz.web.api.clinic;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petz.domain.ClinicUser;
import org.majimena.petz.domain.User;
import org.majimena.petz.domain.clinic.ClinicUserAuthorizationToken;
import org.majimena.petz.domain.clinic.ClinicUserCriteria;
import org.majimena.petz.domain.user.SignupRegistry;
import org.majimena.petz.domain.user.UserPatchRegistry;
import org.majimena.petz.service.ClinicUserService;
import org.majimena.petz.service.UserService;
import org.majimena.petz.web.api.user.SignupRegistryValidator;
import org.majimena.petz.web.api.user.UserPatchRegistryValidator;
import org.majimena.petz.web.rest.util.PaginationUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class ClinicUserController {

    @Inject
    private ClinicUserService clinicUserService;

    @Inject
    private UserService userService;

    @Inject
    private SignupRegistryValidator signupRegistryValidator;

    @Inject
    private UserPatchRegistryValidator userPatchRegistryValidator;

    public void setClinicUserService(ClinicUserService clinicUserService) {
        this.clinicUserService = clinicUserService;
    }

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
    public ResponseEntity<List<ClinicUser>> getAll(@PathVariable String clinicId,
                                             @RequestParam(value = "page", required = false) Integer offset, @RequestParam(value = "per_page", required = false) Integer limit,
                                             @Valid ClinicUserCriteria criteria) throws URISyntaxException {
        Pageable pageable = PaginationUtil.generatePageRequest(offset, limit);
        criteria.setClinicId(clinicId);

        Page<ClinicUser> users = clinicUserService.getUsersByClinicUserCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(users, "/api/v1/clinics/" + clinicId + "/users", offset, limit);
        return new ResponseEntity<>(users.getContent(), headers, HttpStatus.OK);
    }

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
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/users", method = RequestMethod.POST)
    public ResponseEntity<Void> post(@PathVariable String clinicId, @RequestBody @Valid SignupRegistry registry, BindingResult errors) throws BindException {
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
    public ResponseEntity<Void> authorize(@PathVariable String clinicId, @PathVariable String userId, @Valid @RequestBody ClinicUserAuthorizationToken token) {
        // 電話番号か氏名で認証
        clinicUserService.authorize(token);
        return ResponseEntity.ok().build();
    }
}
