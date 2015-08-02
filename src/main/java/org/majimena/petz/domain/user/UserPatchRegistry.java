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
 * Created by todoken on 2015/08/02.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPatchRegistry implements Serializable {

    private String userId;

    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    @NotEmpty
    @Email
    @Size(max = 100)
    private String email;

}
