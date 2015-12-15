package org.majimena.petz.web.api.me;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petz.domain.User;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Optional;

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
        Optional<User> user = userService.getUserByUserId(userId);
        return user.map(u -> ResponseEntity.ok(u))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
