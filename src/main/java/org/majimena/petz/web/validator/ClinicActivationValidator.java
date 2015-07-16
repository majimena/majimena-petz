package org.majimena.petz.web.validator;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petz.domain.ClinicInvitation;
import org.majimena.petz.domain.errors.ErrorCode;
import org.majimena.petz.repository.ClinicInvitationRepository;
import org.majimena.petz.security.SecurityUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import javax.inject.Inject;
import java.util.Optional;

/**
 * Created by todoken on 2015/07/13.
 */
@Component
public class ClinicActivationValidator extends AbstractValidator<String> {

    @Inject
    private ClinicInvitationRepository clinicInvitationRepository;

    public void setClinicInvitationRepository(ClinicInvitationRepository clinicInvitationRepository) {
        this.clinicInvitationRepository = clinicInvitationRepository;
    }

    @Override
    protected void validate(Optional<String> target, Errors errors) {
        Optional<ClinicInvitation> invitation = target.map(id -> clinicInvitationRepository.findOne(id));

        // 招待状がなくなっていないかチェック
        if (!invitation.isPresent()) {
            errors.reject(ErrorCode.PTZ_001201.name());
        }

        // 自分に送られた招待状であるかチェック
        invitation.ifPresent(i -> {
            String login = SecurityUtils.getCurrentLogin();
            if (!StringUtils.equals(i.getEmail(), login)) {
                errors.reject(ErrorCode.PTZ_001202.name());
            }
        });
    }
}
