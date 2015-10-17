package org.majimena.petz.repository.spec;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petz.datetime.LocalDateTimeProvider;
import org.majimena.petz.domain.Schedule;
import org.majimena.petz.domain.examination.ScheduleCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * スケジュールを検索するスペック.
 */
public class ScheduleCriteriaSpec implements Specification<Schedule> {

    /**
     * スペック.
     */
    private Specification<Schedule> specification;

    /**
     * コンストラクタ.
     *
     * @param criteria スケジュールクライテリア
     */
    public ScheduleCriteriaSpec(ScheduleCriteria criteria) {
        this.specification = Specifications
                .where(equalClinicId(criteria))
                .and(betweenStartDateTimeAndEndDateTime(criteria));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Predicate toPredicate(Root<Schedule> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        query.orderBy(cb.asc(root.get("startDateTime")));
        return specification.toPredicate(root, query, cb);
    }

    private Specification equalClinicId(ScheduleCriteria criteria) {
        if (StringUtils.isEmpty(criteria.getClinicId())) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("clinic").get("id"), criteria.getClinicId());
    }

    private Specification betweenStartDateTimeAndEndDateTime(ScheduleCriteria criteria) {
        // 日付まで指定されていれば日付、そうでなければ範囲を月にする
        LocalDateTime from = Optional.ofNullable(criteria.getDay())
                .map(p -> LocalDateTimeProvider.of(criteria.getYear(), criteria.getMonth(), p))
                .orElse(LocalDateTimeProvider.of(criteria.getYear(), criteria.getMonth()));
        LocalDateTime to = Optional.ofNullable(criteria.getDay())
                .map(p -> from.plusDays(1))
                .orElse(from.plusMonths(1));
        return (root, query, cb) ->
                cb.or(cb.between(root.get("startDateTime"), from, to.minusSeconds(1)), cb.between(root.get("endDateTime"), from.plusSeconds(1), to));
    }
}
