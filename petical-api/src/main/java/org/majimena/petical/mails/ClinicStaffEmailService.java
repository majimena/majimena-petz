package org.majimena.petical.mails;

import org.majimena.petical.domain.ClinicInvitation;

/**
 * クリニックスタッフメールサービス.
 */
public interface ClinicStaffEmailService {

    /**
     * 招待メールを送ります.
     *
     * @param email      送信先メールアドレス
     * @param invitation 招待メール
     */
    void sendInvitationMail(String email, ClinicInvitation invitation);

}
