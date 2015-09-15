package org.majimena.petz.repository.spec;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petz.domain.Chart;
import org.majimena.petz.domain.chart.ChartCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

import javax.persistence.criteria.Order;

/**
 * カルテスペック.
 */
public class ChartSpecs {

    public static Specification<Chart> of(ChartCriteria criteria) {
        return Specifications
            .where(clinicId(criteria.getClinicId()))
            .and(customerId(criteria.getCustomerId()));
    }

    private static Specification clinicId(String clinicId) {
        if (StringUtils.isEmpty(clinicId)) {
            return null;
        }
        return (root, query, cb) -> {
            query.orderBy(cb.asc(root.get("chartNo")));
            return cb.equal(root.get("clinic").get("id"), clinicId);
        };
    }

    private static Specification customerId(String customerId) {
        if (StringUtils.isEmpty(customerId)) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("customer").get("id"), customerId);
    }
}
