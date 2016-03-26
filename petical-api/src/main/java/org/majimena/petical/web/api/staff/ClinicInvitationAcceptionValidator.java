package org.majimena.petical.web.api.staff;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petical.domain.ClinicInvitation;
import org.majimena.petical.domain.User;
import org.majimena.petical.domain.clinic.ClinicInvitationAcception;
import org.majimena.petical.domain.errors.ErrorCode;
import org.majimena.petical.repository.ClinicInvitationRepository;
import org.majimena.petical.security.SecurityUtils;
import org.majimena.petical.web.api.AbstractValidator;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Optional;

/**
 * クリニック招待受け入れドメインのバリデータ.
 */
@Named("clinicInvitationAcceptionValidator")
public class ClinicInvitationAcceptionValidator extends AbstractValidator<ClinicInvitationAcception> {

    @Inject
    private ClinicInvitationRepository clinicInvitationRepository;

    @Override
    @Transactional(readOnly = true)
    protected void validate(Optional<ClinicInvitationAcception> target, Errors errors) {
        target.ifPresent(acception -> {
            // 招待状がなくなっていないかチェック
            ClinicInvitation invitation = clinicInvitationRepository.findOne(acception.getClinicInvitationId());
            if (invitation == null) {
                errors.reject(ErrorCode.PTZ_001201.name());
                return;
            }

            // 自分に送られた招待状であるかチェック
            User invited = invitation.getInvitedUser();
            if (invited != null) {
                if (!StringUtils.equals(SecurityUtils.getCurrentUserId(), invited.getId())) {
                    errors.reject(ErrorCode.PTZ_001202.name());
                }
            }
        });
    }
}
