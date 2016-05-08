package org.majimena.petical.mails.impl;

import org.majimena.petical.common.provider.EmailProvider;
import org.majimena.petical.domain.ClinicInvitation;
import org.majimena.petical.mails.ClinicStaffEmailService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * クリニックスタッフメールサービスの実装クラス.
 */
@Service
public class ClinicStaffEmailServiceImpl implements ClinicStaffEmailService {

    /**
     * メールマネジャー.
     */
    @Inject
    private EmailProvider emailProvider;

    /**
     * {@inheritDoc}
     */
    @Async
    @Override
    public void sendInvitationMail(String email, ClinicInvitation invitation) {
        Map<String, Object> params = new HashMap<>();
        params.put("email", email);
        params.put("invitation", invitation);

        String subject = String.format("%sから招待状が届きました", invitation.getClinic().getName());
        if (invitation.getInvitedUser() != null) {
            // 既存ユーザーに招待メールを送る
            emailProvider.sendEmail(email, subject, "staff/ClinicStaffInvitation", params);
        } else {
            // 新規ユーザーに招待メールを送る
            emailProvider.sendEmail(email, subject, "staff/ClinicStaffInvitationNewUser", params);
        }
    }
}
