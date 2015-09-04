package org.majimena.petz.repository;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petz.domain.ClinicUser;
import org.majimena.petz.domain.clinic.ClinicUserCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Optional;

/**
 * クリニックユーザーリポジトリ.
 */
public interface ClinicUserRepository extends JpaRepository<ClinicUser, String>, JpaSpecificationExecutor<ClinicUser> {

    Optional<ClinicUser> findByClinicIdAndUserId(String clinicId, String userId);

    @Deprecated
    @Query("select cu from ClinicUser cu where cu.clinic.id = :clinicId order by cu.user.lastName, cu.user.firstName, cu.user.id")
    Page<ClinicUser> findUsersByClinicId(@Param("clinicId") String clinicId, Pageable pageable);

    class Spec {

        public static Specification<ClinicUser> of(ClinicUserCriteria criteria) {
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
