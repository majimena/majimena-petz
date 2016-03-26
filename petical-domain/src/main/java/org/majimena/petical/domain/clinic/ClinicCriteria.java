package org.majimena.petical.domain.clinic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * クリニッククライテリア.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClinicCriteria implements Serializable {

    /**
     * ユーザーID.
     */
    private String userId;

}
