package org.majimena.petical.repository.spec;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petical.domain.Customer;
import org.majimena.petical.domain.customer.CustomerCriteria;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Created by todoken on 2015/09/22.
 */
public class CustomerSpecs implements Specification<Customer> {

    /**
     * チケット添付ファイルのクライテリアのスペックを取得する.
     *
     * @param clinicId クリニックID
     * @return スペック
     */
    public static Specification<Customer> of(String clinicId) {
        return Specifications
                .where(CustomerSpecs.equalClinicId(clinicId));
    }

    /**
     * チケット添付ファイルのソート条件を取得する.
     *
     * @return ソート条件
     */
    public static Sort asc() {
        return new Sort(Sort.Direction.ASC, "createdDate", "customerCode");
    }

    /**
     * クリニックIDと完全一致するスペックを取得する.
     *
     * @param clinicId クリニックID
     * @return スペック
     */
    private static Specification equalClinicId(String clinicId) {
        return (root, query, cb) -> cb.equal(root.get("clinic").get("id"), clinicId);
    }

    // FIXME 以下は廃止処理

    private Specification<Customer> specification;

    public CustomerSpecs(CustomerCriteria criteria) {
        this.specification = Specifications
            .where(equalClinicId(criteria.getClinicId()))
            .and(likeAfterLogin(criteria.getLogin()))
            .and(likeAfterEmail(criteria.getEmail()))
            .and(likeAnywhereCustomerName(criteria));
    }

    private Specification likeAfterLogin(String login) {
        if (StringUtils.isEmpty(login)) {
            return null;
        }
        return (root, query, cb) -> cb.like(
            root.get("user").get("login"), login + "%");
    }

    private Specification likeAfterEmail(String email) {
        if (StringUtils.isEmpty(email)) {
            return null;
        }
        return (root, query, cb) -> cb.like(
            root.get("user").get("login"), email + "%");
    }

    private Specification likeAnywhereCustomerName(CustomerCriteria criteria) {
        if (StringUtils.isEmpty(criteria.getName())) {
            return null;
        }
        return (root, query, cb) -> cb.or(
            cb.like(root.get("lastName"), "%" + criteria.getName() + "%"),
            cb.like(root.get("firstName"), "%" + criteria.getName() + "%")
        );
    }

    @Override
    public Predicate toPredicate(Root<Customer> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return specification.toPredicate(root, query, cb);
    }
}
