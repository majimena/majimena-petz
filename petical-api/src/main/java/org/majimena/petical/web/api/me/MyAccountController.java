package org.majimena.petical.web.api.me;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petical.domain.User;
import org.majimena.petical.domain.user.PasswordRegistry;
import org.majimena.petical.security.SecurityUtils;
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

/**
 * マイアカウントのコントローラ.
 */
@RestController
@RequestMapping("/api/v1")
public class MyAccountController {

    /**
     * ユーザーサービス.
     */
    @Inject
    private UserService userService;

    /**
     * ログインユーザーのアカウント情報を取得する.
     *
     * @return レスポンスエンティティ（通常時は200、ログインユーザーが見つからない場合は404）
     */
    @Timed
    @RequestMapping(value = "/me", method = RequestMethod.GET)
    public ResponseEntity<User> get() {
        String userId = SecurityUtils.getCurrentUserId();
        return userService.getUserByUserId(userId)
            .map(u -> ResponseEntity.ok(u))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * アカウント情報を更新する.
     *
     * @param user   ユーザー情報
     * @param errors エラー
     * @return レスポンスエンティティ（通常時は200、入力エラー時は400）
     * @throws BindException 入力エラー時に発生する例外
     */
    @Timed
    @RequestMapping(value = "/me", method = RequestMethod.PUT)
    public ResponseEntity<User> put(@Valid @RequestBody User user, BindingResult errors) throws BindException {
        ErrorsUtils.rejectIfEmpty("username", user.getUsername(), errors);
        ErrorsUtils.throwIfHasErrors(errors);

        user.setId(SecurityUtils.getCurrentUserId());
        User save = userService.updateUser(user);
        return ResponseEntity.ok().body(save);
    }

    /**
     * パスワードを更新する.
     *
     * @param registry パスワードレジストリ
     * @param errors   エラー
     * @return レスポンスエンティティ（通常時は200、入力エラー時は400）
     * @throws BindException 入力エラー時に発生する例外
     */
    @Timed
    @RequestMapping(value = "/me/password", method = RequestMethod.PUT)
    public ResponseEntity<Void> putPassword(@Valid @RequestBody PasswordRegistry registry, BindingResult errors) throws BindException {
        ErrorsUtils.throwIfHasErrors(errors);

        registry.setUserId(SecurityUtils.getCurrentUserId());
        userService.changePassword(registry);
        return ResponseEntity.ok().build();
    }
}
