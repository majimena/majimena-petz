package org.majimena.petical.domain.clinic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * クリニック招待状レジストリ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClinicInvitationRegistry implements Serializable {

    private String clinicId;

    @NotNull
    @Size(min = 1, max = 20)
    private String[] emails;

}
