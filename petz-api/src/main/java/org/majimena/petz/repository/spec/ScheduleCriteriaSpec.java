package org.majimena.petz.repository.spec;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petz.datatype.TicketStatus;
import org.majimena.petz.datetime.L10nDateTimeProvider;
import org.majimena.petz.domain.Ticket;
import org.majimena.petz.domain.examination.TicketCriteria;
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
public class ScheduleCriteriaSpec implements Specification<Ticket> {

    /**
     * スペック.
     */
    private Specification<Ticket> specification;

    /**
     * コンストラクタ.
     *
     * @param criteria スケジュールクライテリア
     */
    public ScheduleCriteriaSpec(TicketCriteria criteria) {
        this.specification = Specifications
                .where(equalClinicId(criteria))
                .and(Optional.ofNullable(criteria.getUserId()).map(ScheduleCriteriaSpec::equalUserId).orElse(null))
                .and(Optional.ofNullable(criteria.getStatus()).map(ScheduleCriteriaSpec::equalStatus).orElse(null))
                .and(betweenStartDateTimeAndEndDateTime(criteria));
    }

    public static Specification equalUserId(String userId) {
        return (root, query, cb) -> cb.equal(root.get("pet").get("user").get("id"), userId);
    }

    public static Specification equalStatus(TicketStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Predicate toPredicate(Root<Ticket> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        query.orderBy(cb.asc(root.get("startDateTime")));
        return specification.toPredicate(root, query, cb);
    }

    private Specification equalClinicId(TicketCriteria criteria) {
        if (StringUtils.isEmpty(criteria.getClinicId())) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("clinic").get("id"), criteria.getClinicId());
    }

    private Specification betweenStartDateTimeAndEndDateTime(TicketCriteria criteria) {
        if (criteria.getYear() == null || criteria.getMonth() == null) {
            return null;
        }

        // 日付まで指定されていれば日付、そうでなければ範囲を月にする
        LocalDateTime from = Optional.ofNullable(criteria.getDay())
                .map(p -> L10nDateTimeProvider.of(criteria.getYear(), criteria.getMonth(), p))
                .orElse(L10nDateTimeProvider.of(criteria.getYear(), criteria.getMonth()));
        LocalDateTime to = Optional.ofNullable(criteria.getDay())
                .map(p -> from.plusDays(1))
                .orElse(from.plusMonths(1));
        return (root, query, cb) ->
                cb.or(cb.between(root.get("startDateTime"), from, to.minusSeconds(1)), cb.between(root.get("endDateTime"), from.plusSeconds(1), to));
    }
}
