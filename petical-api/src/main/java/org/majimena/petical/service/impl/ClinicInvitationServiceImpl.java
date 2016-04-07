package org.majimena.petical.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petical.common.exceptions.ApplicationException;
import org.majimena.petical.common.exceptions.SystemException;
import org.majimena.petical.common.utils.ExceptionUtils;
import org.majimena.petical.common.utils.RandomUtils;
import org.majimena.petical.domain.Clinic;
import org.majimena.petical.domain.ClinicInvitation;
import org.majimena.petical.domain.ClinicStaff;
import org.majimena.petical.domain.User;
import org.majimena.petical.domain.errors.ErrorCode;
import org.majimena.petical.domain.user.Roles;
import org.majimena.petical.manager.MailManager;
import org.majimena.petical.repository.ClinicInvitationRepository;
import org.majimena.petical.repository.ClinicRepository;
import org.majimena.petical.repository.ClinicStaffRepository;
import org.majimena.petical.repository.UserRepository;
import org.majimena.petical.security.SecurityUtils;
import org.majimena.petical.service.ClinicInvitationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * クリニック招待サービスの実装クラス.
 */
@Service
public class ClinicInvitationServiceImpl implements ClinicInvitationService {

    @Inject
    private ClinicRepository clinicRepository;

    @Inject
    private ClinicInvitationRepository clinicInvitationRepository;

    @Inject
    private ClinicStaffRepository clinicStaffRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private MailManager mailManager;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<ClinicInvitation> getClinicInvitationsByUserId(String userId) {
        List<ClinicInvitation> invitations = new ArrayList<>();

        // ユーザIDが一致する招待状を取得する
        List<ClinicInvitation> invitations1 = clinicInvitationRepository.findByInvitedUserId(userId);
        invitations.addAll(invitations1);

        // メールアドレス（ログインID）が一致する招待状を取得する
        User one = userRepository.findOne(userId);
        List<ClinicInvitation> invitations2 = clinicInvitationRepository.findByEmail(one.getLogin());
        invitations.addAll(invitations2);

        return invitations;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<ClinicInvitation> getClinicInvitationById(String invitationId) {
        ClinicInvitation invitation = clinicInvitationRepository.findOne(invitationId);
        return Optional.ofNullable(invitation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void inviteStaff(String clinicId, String userId, Set<String> emails) {
        // クリニックと招待状送信ユーザーを取得する
        Clinic clinic = clinicRepository.findOne(clinicId);
        User user = userRepository.findOne(userId);
        ExceptionUtils.throwIfNull(user);
        ExceptionUtils.throwIfNull(clinic);

        // 送信先を特定する
        emails.stream().forEach(email -> {
            // 招待先が既存ユーザーならユーザーを取得
            Optional<User> invited = userRepository.findOneByLogin(email);

            // クリニック招待状を作成する
            String activationKey = RandomUtils.generateActivationKey();
            ClinicInvitation invitation = ClinicInvitation.builder()
                    .clinic(clinic)
                    .user(user)
                    .invitedUser(invited.orElse(null))
                    .email(email)
                    .activationKey(activationKey).build();
            clinicInvitationRepository.save(invitation);

            // 招待メールを送信
            Map<String, Object> params = new HashMap<>();
            params.put("email", email);
            params.put("clinic", clinic);
            params.put("user", user);
            params.put("invitedUser", invited.orElse(null));
            params.put("invitation", invitation);
            params.put("activationKey", activationKey);
            if (invited.isPresent()) {
                // 既存ユーザーに招待メールを送る
                String subject = String.format("[重要] %sから招待状が届きました", clinic.getName());
                mailManager.sendEmail(email, subject, "invitation1", params);
            } else {
                // 新規ユーザーに招待メールを送る
                String subject = String.format("[重要] %sから招待状が届きました", clinic.getName());
                mailManager.sendEmail(email, subject, "invitation2", params);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void activate(String invitationId, String activationKey) {
        String login = SecurityUtils.getCurrentLogin();
        ClinicInvitation invitation = clinicInvitationRepository.findOne(invitationId);

        // 招待を承認してアクティベートする（validation済みであること）
        Optional<User> user = userRepository.findOneByLogin(login);
        ClinicStaff staff = ClinicStaff.builder()
                .clinic(invitation.getClinic())
                .user(user.orElseThrow(() -> new SystemException("get unsaved user.")))
                .role(Roles.ROLE_STAFF.name())
                .activatedDate(LocalDate.now()).build();

        // アクティベーションキーの入力間違いがないかチェックする
        if (!StringUtils.equals(activationKey, invitation.getActivationKey())) {
            throw new ApplicationException(ErrorCode.PTZ_001203);
        }

        clinicStaffRepository.save(staff);
        clinicInvitationRepository.delete(invitation);
    }
}
