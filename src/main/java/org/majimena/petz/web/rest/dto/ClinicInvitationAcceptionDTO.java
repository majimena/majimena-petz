package org.majimena.petz.web.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
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

    @NotNull
    @Size(min = 5, max = 50)
    private String email;

    @NotNull
    @Size(min = 5, max = 100)
    private String activationKey;

}
