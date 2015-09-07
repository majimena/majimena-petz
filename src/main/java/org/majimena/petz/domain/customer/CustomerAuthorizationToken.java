package org.majimena.petz.domain.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import org.majimena.petz.datatypes.defs.ID;
import org.majimena.petz.datatypes.defs.PhoneNo;

import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Created by todoken on 2015/08/27.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerAuthorizationToken implements Serializable {

    @NotEmpty
    @Size(max = ID.MAX_LENGTH)
    private String clinicId;

    @NotEmpty
    @Size(max = ID.MAX_LENGTH)
    private String userId;

    @NotEmpty
    @Size(max = PhoneNo.MAX_LENGTH)
    private String phoneNo;

}
