package org.majimena.petz.web.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * クリニック招待状承認用リクエスト.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClinicInvitationAcceptionDTO implements Serializable {

    @NotEmpty
    @Email
    @Size(min = 5, max = 50)
    private String email;

    @NotEmpty
    @Size(min = 20, max = 20)
    private String activationKey;

}
