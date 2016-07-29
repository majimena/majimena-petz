package org.majimena.petical.repository.spec;

import org.majimena.petical.domain.Customer;
import org.majimena.petical.domain.Pet;
import org.majimena.petical.domain.clinic.ClinicPetCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

import javax.persistence.criteria.*;

/**
 * クリニック管理化のペットを検索するスペック.
 */
public class ClinicPetCriteriaSpec implements Specification<Pet> {

    private Specification<Pet> specification;

    public ClinicPetCriteriaSpec(ClinicPetCriteria criteria) {
        this.specification = Specifications
            .where(equalClinicId(criteria.getClinicId()));
    }

    private Specification equalClinicId(String clinicId) {
        return (root, query, cb) -> {
            Subquery<String> subQuery = query.subquery(String.class);
            Root<Customer> customer = subQuery.from(Customer.class);
            subQuery.select(customer.get("user").get("id"));
            subQuery.where(cb.equal(customer.get("clinic").get("id"), clinicId));
            return root.get("user").get("id").in(subQuery);
        };
    }

    @Override
    public Predicate toPredicate(Root<Pet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return specification.toPredicate(root, query, cb);
    }
}
