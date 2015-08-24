package org.majimena.petz.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * ユーザークライテリア.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCriteria implements Serializable {

    private String email;

}
