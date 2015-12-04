package org.majimena.petz.web.api.clinic;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petz.domain.ClinicInvitation;
import org.majimena.petz.domain.clinic.ClinicInvitationAcception;
import org.majimena.petz.domain.errors.ErrorCode;
import org.majimena.petz.repository.ClinicInvitationRepository;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.web.api.AbstractValidator;
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
    protected void validate(Optional<ClinicInvitationAcception> target, Errors errors) {
        target.ifPresent(acception -> {
            // 招待状がなくなっていないかチェック
            ClinicInvitation invitation = clinicInvitationRepository.findOne(acception.getClinicInvitationId());
            if (invitation == null) {
                errors.reject(ErrorCode.PTZ_001201.name());
                return;
            }

            // 自分に送られた招待状であるかチェック
            String userId = invitation.getUser().getId();
            if (!StringUtils.equals(SecurityUtils.getCurrentUserId(), userId)) {
                errors.reject(ErrorCode.PTZ_001202.name());
            }
        });
    }
}
