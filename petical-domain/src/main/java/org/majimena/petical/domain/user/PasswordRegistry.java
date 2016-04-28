package org.majimena.petical.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * パスワードレジストリ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordRegistry implements Serializable {

    private String userId;

    @Size(min = 5, max = 100)
    private String resetKey;

    @NotEmpty
    @Size(min = 5, max = 100)
    private String oldPassword;

    @NotEmpty
    @Size(min = 5, max = 100)
    private String newPassword;

}
