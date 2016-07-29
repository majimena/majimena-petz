package org.majimena.petical.web.api.staff;

import org.apache.commons.validator.GenericValidator;
import org.majimena.petical.domain.clinic.ClinicInvitationRegistry;
import org.majimena.petical.domain.errors.ErrorCode;
import org.majimena.petical.repository.ClinicStaffRepository;
import org.majimena.petical.repository.UserRepository;
import org.majimena.petical.web.api.AbstractValidator;
import org.springframework.validation.Errors;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Optional;

/**
 * クリニック招待状送付バリデータ.
 */
@Named("clinicInvitationRegistryValidator")
public class ClinicInvitationRegistryValidator extends AbstractValidator<ClinicInvitationRegistry> {

    @Inject
    private UserRepository userRepository;

    @Inject
    private ClinicStaffRepository clinicStaffRepository;

    @Override
    protected void validate(Optional<ClinicInvitationRegistry> target, Errors errors) {
        target.ifPresent(registry -> {
            // メールアドレスのチェック
            validateEmails(registry, errors);
        });
    }

    private void validateEmails(ClinicInvitationRegistry target, Errors errors) {
        for (String email : target.getEmails()) {
            // メールアドレスの妥当性チェック
            if (!GenericValidator.isEmail(email)) {
                errors.rejectValue("emails", "errors.validation.email", "not a well-formed email address");
                return;
            }

            // 同一メールアドレスが既にスタッフとして登録されていないかチェック
            userRepository.findOneByActivatedIsTrueAndLogin(email).ifPresent(user ->
                    clinicStaffRepository.findByClinicIdAndUserId(target.getClinicId(), user.getId())
                            .ifPresent(staff -> errors.rejectValue("emails", ErrorCode.PTZ_001204.name())));
        }
    }
}
