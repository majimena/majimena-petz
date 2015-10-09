package org.majimena.petz.repository.spec;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petz.domain.Schedule;
import org.majimena.petz.domain.examination.ScheduleCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;

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
        return specification.toPredicate(root, query, cb);
    }

    private Specification equalClinicId(ScheduleCriteria criteria) {
        if (StringUtils.isEmpty(criteria.getClinicId())) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("clinic").get("id"), criteria.getClinicId());
    }

    private Specification betweenStartDateTimeAndEndDateTime(ScheduleCriteria criteria) {
        if (criteria.getDay() != null) {
            // 日付指定
            LocalDate date = LocalDate.of(criteria.getYear(), criteria.getMonth(), criteria.getDay());
            LocalDateTime from = date.atStartOfDay();
            LocalDateTime to = date.atTime(LocalTime.MAX);
            return (root, query, cb) ->
                cb.or(cb.between(root.get("startDateTime"), from, to), cb.between(root.get("endDateTime"), from.plusMinutes(1), to));
        } else {
            // 年月のみの指定
            YearMonth ym = YearMonth.of(criteria.getYear(), criteria.getMonth());
            LocalDateTime from = ym.atDay(1).atStartOfDay();
            LocalDateTime to = ym.atEndOfMonth().atTime(LocalTime.MAX);
            return (root, query, cb) ->
                cb.or(cb.between(root.get("startDateTime"), from, to), cb.between(root.get("endDateTime"), from.plusMinutes(1), to));
        }
    }
}
