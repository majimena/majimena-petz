package org.majimena.petical.web.api;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petical.domain.user.PasswordResetRegistry;
import org.majimena.petical.service.UserService;
import org.majimena.petical.web.utils.ErrorsUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;

/**
 * パスワードをリセットするコントローラ.
 */
@RestController
@RequestMapping("/api/v1")
public class PasswordResetController {

    /**
     * ユーザーサービス.
     */
    @Inject
    private UserService userService;

    /**
     * パスワードをリセットする要求を出します.<br/>
     * この処理をすると、パスワードをリセットするためのキーを記載したメールを送信します.
     *
     * @param login パスワードをリセットするユーザーのログインID
     * @return リクエストエンティティ（成功時：200、失敗時：404、異常終了時：500）
     */
    @Timed
    @RequestMapping(value = "/password", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@RequestParam String login) {
        // リクエストパラメータをチェックする
        ErrorsUtils.throwIfEmpty(login);
        ErrorsUtils.throwIfNotMailAddress(login);

        // パスワードリセットのお知らせを送る（ユーザーがいないのがわかるので、常にOKで返す）
        return userService.requestPasswordReset(login)
                .map(user -> ResponseEntity.ok().build())
                .orElse(ResponseEntity.ok().build());
    }

    /**
     * パスワードをリセットします.<br/>
     * パスワードをリセットするキーは送信してから１時間のみ有効です.
     *
     * @param registry パスワードリセットレジストリ
     * @return リクエストエンティティ（成功時：200、失敗時：404、異常終了時：500）
     */
    @Timed
    @RequestMapping(value = "/password", method = RequestMethod.POST)
    public ResponseEntity<Void> post(@RequestBody @Valid PasswordResetRegistry registry) {
        return userService.resetPassword(registry.getNewPassword(), registry.getResetKey())
                .map(user -> ResponseEntity.ok().build())
                .orElse(ResponseEntity.notFound().build());
    }
}
