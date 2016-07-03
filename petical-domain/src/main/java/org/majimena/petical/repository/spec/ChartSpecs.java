package org.majimena.petical.repository.spec;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petical.domain.Chart;
import org.majimena.petical.domain.chart.ChartCriteria;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.JoinType;

/**
 * カルテスペック.
 */
public class ChartSpecs {

    /**
     * クリニックIDをもとに、スペックを取得する.
     *
     * @param clinicId クリニックID
     * @return スペック
     */
    public static Specification<Chart> of(String clinicId) {
        return Specifications
                .where((root, query, builder) -> {
                    // 性能向上のため一回のクエリで取得する
                    Fetch<Object, Object> pet = root.fetch("pet", JoinType.INNER);
                    pet.fetch("kind", JoinType.LEFT);
                    pet.fetch("type", JoinType.LEFT);
                    pet.fetch("color", JoinType.LEFT);
                    pet.fetch("blood", JoinType.LEFT);
                    Fetch<Object, Object> customer = root.fetch("customer", JoinType.INNER);
                    customer.fetch("user", JoinType.INNER);
                    return builder.equal(root.get("clinic").get("id"), clinicId);
                });
    }

    /**
     * カルテ番号、作成日時の昇順でソートする.
     *
     * @return ソート条件
     */
    public static Sort asc() {
        return new Sort(Sort.Direction.ASC, "chartNo", "createdDate");
    }

    @Deprecated
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
