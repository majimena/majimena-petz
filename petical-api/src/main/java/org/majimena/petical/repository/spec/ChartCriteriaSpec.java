package org.majimena.petical.repository.spec;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petical.domain.Chart;
import org.majimena.petical.domain.chart.ChartCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * カルテスペック.
 */
public class ChartCriteriaSpec implements Specification<Chart> {

    private Specification<Chart> specification;

    public ChartCriteriaSpec(ChartCriteria criteria) {
        this.specification = Specifications
            .where(equalClinicId(criteria))
            .and(equalPetId(criteria))
            .and(equalCustomerId(criteria))
            .and(likeAnywhereCustomerName(criteria));
    }

    private Specification equalClinicId(ChartCriteria criteria) {
        return (root, query, cb) -> {
            query.orderBy(cb.asc(root.get("chartNo")));
            return cb.equal(root.get("clinic").get("id"), criteria.getClinicId());
        };
    }

    private Specification equalPetId(ChartCriteria criteria) {
        if (StringUtils.isEmpty(criteria.getPetId())) {
            return null;
        }
        return (root, query, cb) ->
            cb.equal(root.get("pet").get("id"), criteria.getPetId());
    }

    private Specification equalCustomerId(ChartCriteria criteria) {
        if (StringUtils.isEmpty(criteria.getCustomerId())) {
            return null;
        }
        return (root, query, cb) ->
            cb.equal(root.get("customer").get("id"), criteria.getCustomerId());
    }

    private Specification likeAnywhereCustomerName(ChartCriteria criteria) {
        if (StringUtils.isEmpty(criteria.getCustomerName()) || StringUtils.isNotEmpty(criteria.getCustomerId())) {
            return null;
        }
        return (root, query, cb) -> cb.or(
            cb.like(root.get("customer").get("lastName"), "%" + criteria.getCustomerName() + "%"),
            cb.like(root.get("customer").get("firstName"), "%" + criteria.getCustomerName() + "%")
        );
    }

    @Override
    public Predicate toPredicate(Root<Chart> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return specification.toPredicate(root, query, cb);
    }
}
