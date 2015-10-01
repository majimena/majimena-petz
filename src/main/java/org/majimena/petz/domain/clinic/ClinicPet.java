package org.majimena.petz.domain.clinic;

import lombok.*;
import org.majimena.petz.domain.Customer;
import org.majimena.petz.domain.Pet;

import java.io.Serializable;

/**
 * クリニックのペットドメイン.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ClinicPet implements Serializable {

    /**
     * シリアルバージョンID.
     */
    private static final long serialVersionUID = -445642867618415821L;

    /**
     * 顧客ドメイン.
     */
    private Customer customer;

    /**
     * ペットドメイン.
     */
    private Pet pet;
}
