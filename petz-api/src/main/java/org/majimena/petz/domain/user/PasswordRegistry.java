package org.majimena.petz.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
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

    @NotNull
    @Size(min = 5, max = 50)
    @Column(length = 50)
    private String oldPassword;

    @NotNull
    @Size(min = 5, max = 50)
    @Column(length = 50)
    private String newPassword;

}
