package org.majimena.petz.domain.clinic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Created by todoken on 2015/08/27.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClinicUserAuthorizationToken implements Serializable {

    @NotEmpty
    private String clinicId;

    @NotEmpty
    private String userId;

    @NotEmpty
    @Size(max = 50)
    private String firstName;

    @NotEmpty
    @Size(max = 50)
    private String lastName;

    @NotEmpty
    @Size(max = 15)
    private String phoneNo;

}
