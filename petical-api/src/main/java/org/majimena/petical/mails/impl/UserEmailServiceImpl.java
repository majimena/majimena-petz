package org.majimena.petical.mails.impl;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petical.common.exceptions.ApplicationException;
import org.majimena.petical.domain.User;
import org.majimena.petical.domain.errors.ErrorCode;
import org.majimena.petical.mails.UserEmailService;
import org.majimena.petical.common.provider.EmailProvider;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * ユーザーメールサービスの実装クラス.
 */
@Service
public class UserEmailServiceImpl implements UserEmailService {

    /**
     * メールマネジャー.
     */
    @Inject
    private EmailProvider emailProvider;

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendActivationMail(User user) {
        // アクティベーションメールを送信する
        String to = user.getEmail();
        Map<String, Object> variables = new HashMap<>();
        variables.put("user", user);
        emailProvider.sendEmail(to, "ユーザー登録完了のお知らせ", "user/ActivationEmail", variables);
    }

    /**
     * {@inheritDoc}
     */
    @Async
    @Override
    public void sendPasswordResetMail(User user) {
        // メールアドレスが未登録の場合は注意喚起（既に会員登録されているのであり得ないエラー）
        if (StringUtils.isEmpty(user.getEmail())) {
            throw new ApplicationException(ErrorCode.PTZ_999801);
        }

        // パスワードリセットメールを送信する
        String to = user.getEmail();
        Map<String, Object> variables = new HashMap<>();
        variables.put("user", user);
        emailProvider.sendEmail(to, "パスワードリセットキーのお知らせ", "user/PasswordResetEmail", variables);
    }
}
