package org.majimena.petz.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.majimena.petz.datatype.defs.MailAddress;
import org.majimena.petz.datatype.defs.Name;

import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * サインアップレジストリ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupRegistry implements Serializable {

    @NotEmpty
    @Size(max = Name.MAX_LENGTH)
    private String firstName;

    @NotEmpty
    @Size(max = Name.MAX_LENGTH)
    private String lastName;

    @NotEmpty
    @Email
    @Size(max = MailAddress.MAX_LENGTH)
    private String email;

    @NotEmpty
    @Size(min = 5, max = 100)
    private String password;
}
