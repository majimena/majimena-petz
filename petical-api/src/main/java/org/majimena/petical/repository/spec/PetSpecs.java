package org.majimena.petical.repository.spec;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petical.domain.Pet;
import org.majimena.petical.domain.pet.PetCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

/**
 * ペットのスペック.
 */
public class PetSpecs {

    public static Specification<Pet> of(PetCriteria criteria) {
        return Specifications
            .where(userId(criteria.getUserId()))
            .and(likeAnywhereName(criteria.getName()));
    }

    private static Specification userId(String userId) {
        if (StringUtils.isEmpty(userId)) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("user").get("id"), userId);
    }

    private static Specification likeAnywhereName(String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        return (root, query, cb) -> cb.like(root.get("name"), "%" + name + "%");
    }
}
