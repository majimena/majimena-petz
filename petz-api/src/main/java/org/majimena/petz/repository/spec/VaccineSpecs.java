package org.majimena.petz.repository.spec;

import org.majimena.petz.domain.Vaccine;
import org.majimena.petz.domain.vaccine.VaccineCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

import java.util.Optional;

/**
 * ワクチン検索スペック.
 */
public class VaccineSpecs {

    public static Specification<Vaccine> of(VaccineCriteria criteria) {
        return Specifications
                .where(Optional.ofNullable(criteria.getClinicId()).map(VaccineSpecs::equalClinicId).orElse(null))
                .and(Optional.ofNullable(criteria.getName()).map(VaccineSpecs::likeAfterName).orElse(null))
                .and(isNotRemoved());
    }

    private static Specification equalClinicId(String clinicId) {
        return (root, query, cb) -> cb.equal(root.get("clinic").get("id"), clinicId);
    }

    private static Specification likeAfterName(String name) {
        return (root, query, cb) -> cb.like(root.get("name"), name + "%");
    }

    private static Specification isNotRemoved() {
        return (root, query, cb) -> cb.equal(root.get("removed"), Boolean.FALSE);
    }
}
