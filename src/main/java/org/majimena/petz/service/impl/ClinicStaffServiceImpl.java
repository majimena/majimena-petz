package org.majimena.petz.service.impl;

import org.majimena.framework.core.managers.EmailManager;
import org.majimena.petz.common.exceptions.SystemException;
import org.majimena.petz.common.utils.RandomUtils;
import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.ClinicInvitation;
import org.majimena.petz.domain.ClinicStaff;
import org.majimena.petz.domain.User;
import org.majimena.petz.domain.user.Roles;
import org.majimena.petz.repository.ClinicInvitationRepository;
import org.majimena.petz.repository.ClinicRepository;
import org.majimena.petz.repository.ClinicStaffRepository;
import org.majimena.petz.repository.UserRepository;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.ClinicStaffService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/**
 * Created by todoken on 2015/06/26.
 */
@Service
@Transactional
public class ClinicStaffServiceImpl implements ClinicStaffService {

    @Inject
    private ClinicRepository clinicRepository;

    @Inject
    private ClinicInvitationRepository clinicInvitationRepository;

    @Inject
    private ClinicStaffRepository clinicStaffRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private SpringTemplateEngine templateEngine;

    @Inject
    private EmailManager emailManager;

    @Value("${mail.from:noreply@petz.io}")
    private String fromEmail;

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setClinicRepository(ClinicRepository clinicRepository) {
        this.clinicRepository = clinicRepository;
    }

    public void setClinicInvitationRepository(ClinicInvitationRepository clinicInvitationRepository) {
        this.clinicInvitationRepository = clinicInvitationRepository;
    }

    public void setClinicStaffRepository(ClinicStaffRepository clinicStaffRepository) {
        this.clinicStaffRepository = clinicStaffRepository;
    }

    public void setTemplateEngine(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public void setEmailManager(EmailManager emailManager) {
        this.emailManager = emailManager;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void inviteStaff(Long clinicId, Set<String> emails) {
        String loginId = SecurityUtils.getCurrentLogin();
        Optional<User> login = userRepository.findOneByLogin(loginId);

        for (String email : emails) {
            // クリニックへの招待待ちとして保存
            String activationKey = RandomUtils.generateActivationKey();
            Clinic clinic = clinicRepository.findOne(clinicId);
            ClinicInvitation invitation = ClinicInvitation.builder()
                .clinic(clinic)
                .user(login.orElseThrow(() -> new SystemException("get unsaved user.")))
                .email(email)
                .activationKey(activationKey).build();
            clinicInvitationRepository.save(invitation);

            // 招待メールを送信
            String locale = login.map(User::getLangKey).orElse("en");
            Context context = new Context(Locale.forLanguageTag(locale));
            context.setVariable("email", email);
            context.setVariable("clinic", clinic);
            context.setVariable("user", login.get());
            context.setVariable("activationKey", activationKey);
            String subject = templateEngine.process("ClinicInvitation-subject", context);
            String content = templateEngine.process("ClinicInvitation-content", context);
            emailManager.send(email, fromEmail, subject, content);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void activate(String activationKey) {
        String login = SecurityUtils.getCurrentLogin();
        ClinicInvitation one = clinicInvitationRepository.findOneByActivationKey(activationKey);
        if (one != null) {
            // クリニックスタッフとして紐付け登録して招待データは削除
            Optional<User> user = userRepository.findOneByLogin(login);
            ClinicStaff staff = ClinicStaff.builder()
                .clinic(one.getClinic())
                .user(user.orElseThrow(() -> new SystemException("get unsaved user.")))
                .role(Roles.ROLE_STAFF.name())
                .activatedDate(LocalDate.now()).build();
            clinicStaffRepository.save(staff);
            clinicInvitationRepository.delete(one);
        }
    }

}
