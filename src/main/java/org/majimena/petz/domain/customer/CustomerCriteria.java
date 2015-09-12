package org.majimena.petz.domain.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;
import org.majimena.petz.domain.common.defs.ID;
import org.majimena.petz.domain.common.defs.MailAddress;

import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 顧客クライテリア.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerCriteria implements Serializable {

    @Size(max = ID.MAX_LENGTH)
    private String clinicId;

    @Email
    @Size(max = MailAddress.MAX_LENGTH)
    private String email;
}
