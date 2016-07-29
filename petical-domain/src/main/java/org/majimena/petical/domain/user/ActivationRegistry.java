package org.majimena.petical.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import org.majimena.petical.datatype.defs.Name;

import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * アクティベーションレジストリ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivationRegistry implements Serializable {
    /**
     * アクティベーションキー.
     */
    @NotEmpty
    private String activationKey;

    /**
     * ユーザー名.
     */
    @NotEmpty
    @Size(max = Name.MAX_LENGTH)
    private String username;

    /**
     * パスワード.
     */
    @NotEmpty
    @Size(min = 6, max = 100)
    private String password;
}
