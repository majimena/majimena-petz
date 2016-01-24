package org.majimena.petz.domain.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.majimena.petz.datatype.defs.MailAddress;
import org.majimena.petz.datatype.defs.Name;
import org.majimena.petz.datatype.defs.PhoneNo;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * ユーザー認証トークン.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CustomerAuthenticationToken implements Serializable {

    private static final long serialVersionUID = 3158586853846056421L;

    private String clinicId;

    @NotEmpty
    @Email
    @Size(max = MailAddress.MAX_LENGTH)
    private String login;

    @NotEmpty
    @Size(max = Name.MAX_LENGTH)
    private String lastName;

    @NotEmpty
    @Size(max = Name.MAX_LENGTH)
    private String firstName;

    @NotEmpty
    @Pattern(regexp = PhoneNo.REGEXP)
    @Size(max = PhoneNo.MAX_LENGTH)
    private String phoneNo;
}
