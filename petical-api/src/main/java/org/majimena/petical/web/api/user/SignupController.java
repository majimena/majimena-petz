package org.majimena.petical.web.api.user;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petical.client.recaptcha.RecaptchaRestAdapterFactory;
import org.majimena.petical.client.recaptcha.SiteVerifyApi;
import org.majimena.petical.client.recaptcha.SiteVerifyEntity;
import org.majimena.petical.domain.User;
import org.majimena.petical.domain.errors.ErrorCode;
import org.majimena.petical.domain.user.SignupRegistry;
import org.majimena.petical.service.UserService;
import org.majimena.petical.web.utils.ErrorsUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.util.stream.Stream;

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

    /**
     * シークレットキー.
     */
    @Value("${google.recaptcha.secret:xxxxxx}")
    private String secret;

    /**
     * サインアップして新規ユーザを登録する.
     *
     * @param captcha  キャプチャ
     * @param registry サインアップレジストリ
     * @param errors   エラー
     * @return レスポンスエンティティ（通常時は201、入力エラー時は400）
     */
    @Timed
    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ResponseEntity<User> post(@RequestParam String captcha, @RequestBody @Valid SignupRegistry registry, BindingResult errors) throws BindException {
        // キャプチャのバリデーション
        SiteVerifyEntity entity = RecaptchaRestAdapterFactory.create()
                .create(SiteVerifyApi.class)
                .post(this.secret, captcha, "");
        if (!entity.isSuccess()) {
            ErrorsUtils.reject(ErrorCode.PTZ_999981, errors);
        }

        // カスタムバリデーションを行う
        signupRegistryValidator.validate(registry, errors);
        ErrorsUtils.throwIfHasErrors(errors);

        // ユーザを新規登録する
        User save = userService.saveUser(registry);
        return ResponseEntity.created(URI.create("/api/v1/users/" + save.getId())).body(save);
    }
}
