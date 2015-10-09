package org.majimena.petz.repository.spec;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petz.domain.Chart;
import org.majimena.petz.domain.chart.ChartCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

/**
 * カルテスペック.
 */
public class ChartSpecs {

    public static Specification<Chart> of(ChartCriteria criteria) {
        return Specifications
            .where(equalClinicId(criteria))
            .and(equalCustomerId(criteria))
            .and(likeAnywhereCustomerName(criteria));
    }

    private static Specification equalClinicId(ChartCriteria criteria) {
        if (StringUtils.isEmpty(criteria.getClinicId())) {
            return null;
        }
        return (root, query, cb) -> {
            query.orderBy(cb.asc(root.get("chartNo")));
            return cb.equal(root.get("clinic").get("id"), criteria.getClinicId());
        };
    }

    private static Specification equalCustomerId(ChartCriteria criteria) {
        if (StringUtils.isEmpty(criteria.getCustomerId())) {
            return null;
        }
        return (root, query, cb) ->
            cb.equal(root.get("customer").get("id"), criteria.getCustomerId());
    }

    private static Specification likeAnywhereCustomerName(ChartCriteria criteria) {
        if (StringUtils.isEmpty(criteria.getCustomerName()) || StringUtils.isNotEmpty(criteria.getCustomerId())) {
            return null;
        }
        return (root, query, cb) -> cb.or(
            cb.like(root.get("customer").get("lastName"), "%" + criteria.getCustomerName() + "%"),
            cb.like(root.get("customer").get("firstName"), "%" + criteria.getCustomerName() + "%")
        );
    }
}
