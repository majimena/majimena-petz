package org.majimena.petz.web.api.user;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petz.domain.User;
import org.majimena.petz.service.UserService;
import org.majimena.petz.web.utils.ErrorsUtils;
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
    private SignupUserValidator signupUserValidator;

    /**
     * サインアップして新規ユーザを登録する.
     *
     * @param user   ユーザ情報
     * @param errors エラー
     * @return レスポンスエンティティ（通常時は201、入力エラー時は400）
     */
    @Timed
    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ResponseEntity<Void> post(@RequestBody @Valid User user, BindingResult errors) throws BindException {
        // カスタムバリデーションを行う
        signupUserValidator.validate(user, errors);
        ErrorsUtils.throwIfHaveErrors(errors);

        // ユーザを新規登録する
        User save = userService.saveUser(user);
        //        mailService.sendActivationEmail(user, ""); // TODO サービス内でメール送信する
        return ResponseEntity.created(URI.create("/api/v1/users/" + save.getId())).build();
    }
}
