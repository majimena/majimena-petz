package org.majimena.petz.repository;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petz.domain.Customer;
import org.majimena.petz.domain.customer.CustomerCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * クリニックユーザーリポジトリ.
 */
public interface CustomerRepository extends JpaRepository<Customer, String>, JpaSpecificationExecutor<Customer> {

    Optional<Customer> findByClinicIdAndUserId(String clinicId, String userId);

    @Deprecated
    @Query("select cu from Customer cu where cu.clinic.id = :clinicId order by cu.user.lastName, cu.user.firstName, cu.user.id")
    Page<Customer> findByClinicId(@Param("clinicId") String clinicId, Pageable pageable);

    class Spec {

        public static Specification<Customer> of(CustomerCriteria criteria) {
            return Specifications
                .where(equalClinicId(criteria.getClinicId()))
                .and(likeAfterEmail(criteria.getEmail()));
        }

        private static Specification equalClinicId(String clinicId) {
            if (StringUtils.isEmpty(clinicId)) {
                return null;
            }
            return (root, query, cb) -> {
                if (query.getResultType() != Long.class) {
                    root.fetch("user");
                }
                return cb.equal(root.get("clinic").get("id"), clinicId);
            };
        }

        private static Specification likeAfterEmail(String email) {
            if (StringUtils.isEmpty(email)) {
                return null;
            }
            return (root, query, cb) -> cb.like(root.get("user").get("login"), email + "%");
        }
    }
}
