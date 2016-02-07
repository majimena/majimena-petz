package org.majimena.petz.domain.clinic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
public class ClinicInvitationAcception implements Serializable {

    private String clinicId;

    private String clinicInvitationId;

    @NotEmpty
    @Size(max = 20)
    private String activationKey;

}
