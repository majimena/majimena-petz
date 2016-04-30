package org.majimena.petical.mails;

import org.majimena.petical.domain.User;

/**
 * ユーザーメールサービス.
 */
public interface UserEmailService {

    /**
     * パスワードリセットのキーを記載したメールを送信します.
     *
     * @param user メール送信先となるユーザ
     */
    void sendPasswordResetMail(User user);

}
