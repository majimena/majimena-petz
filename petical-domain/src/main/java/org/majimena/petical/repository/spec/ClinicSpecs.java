package org.majimena.petical.repository.spec;

import org.majimena.petical.domain.Clinic;
import org.majimena.petical.domain.clinic.ClinicCriteria;
import org.springframework.data.jpa.domain.Specification;

/**
 * クリニック検索スペック.
 */
public class ClinicSpecs {

    public static Specification<Clinic> of(ClinicCriteria criteria) {
        return null;
    }

}
