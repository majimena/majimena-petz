package org.majimena.petz.domain.clinic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * クリニックユーザークライテリア.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClinicUserCriteria implements Serializable {

    @Size(max = 50)
    private String clinicId;

    @Email
    @Size(max = 100)
    private String email;

}
