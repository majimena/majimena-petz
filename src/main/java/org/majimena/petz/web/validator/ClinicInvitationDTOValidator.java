package org.majimena.petz.web.validator;

import org.apache.commons.validator.GenericValidator;
import org.majimena.petz.web.rest.dto.ClinicInvitationDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.Optional;

/**
 * Created by todoken on 2015/06/25.
 */
@Component
public class ClinicInvitationDTOValidator extends AbstractValidator<ClinicInvitationDTO> {

    @Override
    protected void validate(Optional<ClinicInvitationDTO> target, Errors errors) {
        target.ifPresent(dto -> validateEmails(dto.getEmails(), errors));
    }

    private void validateEmails(String[] emails, Errors errors) {
        for (String email : emails) {
            if (!GenericValidator.isEmail(email)) {
                errors.rejectValue("emails", "errors.validation.email", "invalid email address");
                return;
            }
        }
    }

}
