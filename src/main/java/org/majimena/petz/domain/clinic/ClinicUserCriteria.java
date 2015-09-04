package org.majimena.petz.domain.clinic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;

import java.io.Serializable;

/**
 * クリニックユーザークライテリア.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClinicUserCriteria implements Serializable {

    private String clinicId;

    @Email
    private String email;

}
