package org.majimena.petical.web.api.user;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petical.domain.User;
import org.majimena.petical.domain.user.ActivationRegistry;
import org.majimena.petical.domain.user.SignupRegistry;
import org.majimena.petical.service.UserService;
import org.majimena.petical.web.utils.ErrorsUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;

/**
 * サインアップコントローラ.
 */
@RestController
@RequestMapping("/api/v1")
public class SignupController {
    /**
     * ユーザサービス.
     */
    @Inject
    private UserService userService;

    /**
     * サインアップ時のユーザバリデータ.
     */
    @Inject
    private SignupRegistryValidator signupRegistryValidator;

    @Timed
    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ResponseEntity<Void> post(@RequestBody @Valid SignupRegistry registry, BindingResult errors) throws BindException {
        // カスタムバリデーションを行う
        signupRegistryValidator.validate(registry, errors);
        ErrorsUtils.throwIfHasErrors(errors);

        // サインアップしてメールを送信する
        userService.signup(registry);
        return ResponseEntity.created(URI.create("/api/v1/signup")).build();
    }

    @Timed
    @RequestMapping(value = "/activate", method = RequestMethod.POST)
    public ResponseEntity<User> activate(@RequestBody @Valid ActivationRegistry registry, BindingResult errors) throws BindException {
        // ユーザーをアクティベートする（キーが間違っていたら権限エラー）
        return userService.activate(registry)
                .map(user -> ResponseEntity.created(URI.create("/api/v1/users/" + user.getId())).body(user))
                .orElse(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
    }
}
