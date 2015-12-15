package org.majimena.petz.repository.spec;

import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.clinic.ClinicCriteria;
import org.springframework.data.jpa.domain.Specification;

/**
 * クリニック検索スペック.
 */
public class ClinicSpecs {

    public static Specification<Clinic> of(ClinicCriteria criteria) {
        return null;
    }
}
