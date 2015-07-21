package org.majimena.petz.web.api.clinic;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petz.common.exceptions.ResourceNotFoundException;
import org.majimena.petz.domain.ClinicInvitation;
import org.majimena.petz.domain.clinic.ClinicInvitationAcception;
import org.majimena.petz.domain.errors.ErrorCode;
import org.majimena.petz.repository.ClinicInvitationRepository;
import org.majimena.petz.repository.ClinicRepository;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.web.api.AbstractValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import javax.inject.Inject;
import java.util.Optional;

/**
 * Created by todoken on 2015/07/13.
 */
@Component
public class ClinicInvitationAcceptionValidator extends AbstractValidator<ClinicInvitationAcception> {

    @Inject
    private ClinicRepository clinicRepository;

    @Inject
    private ClinicInvitationRepository clinicInvitationRepository;

    public void setClinicRepository(ClinicRepository clinicRepository) {
        this.clinicRepository = clinicRepository;
    }

    public void setClinicInvitationRepository(ClinicInvitationRepository clinicInvitationRepository) {
        this.clinicInvitationRepository = clinicInvitationRepository;
    }

    @Override
    protected void validate(Optional<ClinicInvitationAcception> target, Errors errors) {
        target.ifPresent(acception -> {
            // クリニックが存在しなければURLの指定ミス
            if (clinicRepository.findOne(acception.getClinicId()) == null) {
                throw new ResourceNotFoundException("clinicId [" + acception.getClinicId() + "] is not found.");
            }

            // 招待状がなくなっていないかチェック
            ClinicInvitation invitation = clinicInvitationRepository.findOne(acception.getClinicInvitationId());
            if (invitation == null) {
                errors.reject(ErrorCode.PTZ_001201.name());
                return;
            }

            // 自分に送られた招待状であるかチェック
            String login = SecurityUtils.getCurrentLogin();
            if (!StringUtils.equals(invitation.getEmail(), login)) {
                errors.reject(ErrorCode.PTZ_001202.name());
            }
        });
    }
}
