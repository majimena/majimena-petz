package org.majimena.petz.repository.spec;

import org.majimena.petz.domain.Product;
import org.majimena.petz.domain.product.ProductCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

import java.util.Optional;

/**
 * プロダクト検索スペック.
 */
public class ProductSpecs {

    public static Specification<Product> of(ProductCriteria criteria) {
        return Specifications
                .where(Optional.ofNullable(criteria.getClinicId()).map(ProductSpecs::equalClinicId).orElse(null))
                .and(Optional.ofNullable(criteria.getName()).map(ProductSpecs::likeAfterName).orElse(null));
    }

    private static Specification equalClinicId(String clinicId) {
        return (root, query, cb) -> cb.equal(root.get("clinic").get("id"), clinicId);
    }

    private static Specification likeAfterName(String name) {
        return (root, query, cb) -> cb.like(root.get("name"), name + "%");
    }
}
