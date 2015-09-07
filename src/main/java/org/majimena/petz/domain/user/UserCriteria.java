package org.majimena.petz.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;
import org.majimena.petz.datatypes.defs.MailAddress;

import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * ユーザークライテリア.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCriteria implements Serializable {

    @Email
    @Size(max = MailAddress.MAX_LENGTH)
    private String email;
}
