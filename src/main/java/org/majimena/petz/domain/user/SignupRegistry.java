package org.majimena.petz.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Created by todoken on 2015/07/20.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupRegistry implements Serializable {

    @NotEmpty
    @Email
    @Size(min = 1, max = 100)
    private String email;

    @NotEmpty
    @Size(min = 5, max = 100)
    private String password;

}
