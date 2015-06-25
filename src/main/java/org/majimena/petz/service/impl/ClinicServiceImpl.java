package org.majimena.petz.service.impl;

import org.majimena.framework.core.managers.EmailManager;
import org.majimena.petz.common.utils.RandomUtils;
import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.ClinicStaff;
import org.majimena.petz.domain.User;
import org.majimena.petz.domain.clinic.ClinicCriteria;
import org.majimena.petz.domain.user.Roles;
import org.majimena.petz.repository.ClinicRepository;
import org.majimena.petz.repository.ClinicStaffRepository;
import org.majimena.petz.repository.UserRepository;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.ClinicService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.inject.Inject;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/**
 * クリニックサービスの実装クラス.
 */
@Service
@Transactional
public class ClinicServiceImpl implements ClinicService {

    @Inject
    private ClinicRepository clinicRepository;

    @Inject
    private ClinicStaffRepository clinicStaffRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private EmailManager emailManager;

    @Inject
    private SpringTemplateEngine templateEngine;

    private String fromEmail;

    public void setClinicRepository(ClinicRepository clinicRepository) {
        this.clinicRepository = clinicRepository;
    }

    public void setClinicStaffRepository(ClinicStaffRepository clinicStaffRepository) {
        this.clinicStaffRepository = clinicStaffRepository;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setEmailManager(EmailManager emailManager) {
        this.emailManager = emailManager;
    }

    public void setTemplateEngine(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Clinic> getClinicById(Long clinicId) {
        return Optional.ofNullable(clinicRepository.findOne(clinicId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Clinic> getClinics(ClinicCriteria criteria, Pageable pageable) {
        return clinicRepository.findAll(pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Clinic> saveClinic(Clinic clinic) {
        String login = SecurityUtils.getCurrentLogin();
        Optional<User> owner = userRepository.findOneByLogin(login);
        owner.ifPresent(u -> {
            clinic.setOwnerUser(u);
            clinicRepository.save(clinic);
        });
        return Optional.of(clinic);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Clinic> updateClinic(Clinic clinic) {
        Optional<Clinic> one = getClinicById(clinic.getId());
        one.ifPresent(p -> {
            p.setName(clinic.getName());
            p.setDescription(clinic.getDescription());
            clinicRepository.save(p);
        });
        return one;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteClinic(Long clinicId) {
        clinicRepository.delete(clinicId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void inviteStaff(Long clinicId, Set<String> emails) {
        String loginId = SecurityUtils.getCurrentLogin();
        Optional<User> login = userRepository.findOneByLogin(loginId);

        for (String email : emails) {
            // クリニックスタッフとして一時保存
            String activationKey = RandomUtils.generateSecureActivationKey();
            Clinic clinic = clinicRepository.findOne(clinicId);
            Optional<User> staff = userRepository.findOneByEmail(email);
            ClinicStaff clinicStaff = ClinicStaff.builder()
                .clinic(clinic)
                .user(staff.orElse(null))
                .email(email)
                .role(Roles.ROLE_STAFF.name())
                .activated(Boolean.FALSE)
                .activationKey(activationKey).build();
            clinicStaffRepository.save(clinicStaff);

            // 招待メールを送信する
            String locale = login.map(User::getLangKey).orElse("en");
            Context context = new Context(Locale.forLanguageTag(locale));
            context.setVariable("email", email);
            context.setVariable("clinic", clinic);
            context.setVariable("activationKey", activationKey);
            String subject = templateEngine.process("ClinicInvitation-subject", context);
            String content = templateEngine.process("ClinicInvitation-content", context);
            emailManager.send(email, fromEmail, subject, content);
        }
    }

}
