package org.majimena.petz.web.api.clinic;

import org.apache.commons.validator.GenericValidator;
import org.majimena.petz.common.exceptions.ResourceNotFoundException;
import org.majimena.petz.domain.clinic.ClinicInvitationRegistry;
import org.majimena.petz.domain.errors.ErrorCode;
import org.majimena.petz.repository.ClinicRepository;
import org.majimena.petz.repository.ClinicStaffRepository;
import org.majimena.petz.repository.UserRepository;
import org.majimena.petz.web.api.AbstractValidator;
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
    private ClinicRepository clinicRepository;

    @Inject
    private ClinicStaffRepository clinicStaffRepository;

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setClinicRepository(ClinicRepository clinicRepository) {
        this.clinicRepository = clinicRepository;
    }

    public void setClinicStaffRepository(ClinicStaffRepository clinicStaffRepository) {
        this.clinicStaffRepository = clinicStaffRepository;
    }

    @Override
    protected void validate(Optional<ClinicInvitationRegistry> target, Errors errors) {
        target.ifPresent(registry -> {
            // クリニックが存在しなければURLの指定ミス
            if (clinicRepository.findOne(registry.getClinicId()) == null) {
                throw new ResourceNotFoundException("clinicId [" + registry.getClinicId() + "] is not found.");
            }

            // 入力されたメールアドレスのチェック
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
            userRepository.findOneByLogin(email)
                .ifPresent(user -> {
                    clinicStaffRepository.findByClinicIdAndUserId(target.getClinicId(), user.getId())
                        .ifPresent(staff -> errors.rejectValue("emails", ErrorCode.PTZ_001204.name()));
                });
        }
    }
}
