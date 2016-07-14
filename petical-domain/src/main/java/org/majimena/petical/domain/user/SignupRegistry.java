package org.majimena.petical.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.majimena.petical.datatype.defs.MailAddress;

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
    /**
     * メールアドレス.
     */
    @NotEmpty
    @Email
    @Size(max = MailAddress.MAX_LENGTH)
    private String email;
}
