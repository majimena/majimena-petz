package org.majimena.petz.repository.spec;

import org.majimena.petz.datatype.TicketState;
import org.majimena.petz.datetime.L10nDateTimeProvider;
import org.majimena.petz.domain.ticket.TicketCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * スケジュールを検索するスペック.
 */
public class TicketSpecs {

    /**
     * チケット検索条件をもとにスペックを作成する.
     *
     * @param criteria チケット検索条件
     * @return スペック
     */
    public static Specification of(TicketCriteria criteria) {
        return Specifications
                .where(Optional.ofNullable(criteria.getClinicId()).map(TicketSpecs::equalClinicId).orElse(null))
                .and(Optional.ofNullable(criteria.getPetId()).map(TicketSpecs::equalPetId).orElse(null))
                .and(Optional.ofNullable(criteria.getUserId()).map(TicketSpecs::equalUserId).orElse(null))
                .and(Optional.ofNullable(criteria.getState()).map(TicketSpecs::equalState).orElse(null))
                .and(betweenStartDateTimeAndEndDateTime(criteria));
    }

    /**
     * 指定した期間中のチケットを検索するスペックを作成する.
     *
     * @param clinicId クリニックID
     * @param from     開始日時（FROM）
     * @param to       終了日時（TO）
     * @return スペック
     */
    public static Specification of(String clinicId, LocalDateTime from, LocalDateTime to) {
        return Specifications
                .where(TicketSpecs.equalClinicId(clinicId))
                .and(TicketSpecs.betweenStartDateTime(from, to));
    }

    public static Specification equalClinicId(String clinicId) {
        return (root, query, cb) -> {
            query.orderBy(cb.asc(root.get("startDateTime")));
            return cb.equal(root.get("clinic").get("id"), clinicId);
        };
    }

    public static Specification equalUserId(String userId) {
        return (root, query, cb) -> cb.equal(root.get("pet").get("user").get("id"), userId);
    }

    public static Specification equalPetId(String petId) {
        return (root, query, cb) -> cb.equal(root.get("pet").get("id"), petId);
    }

    public static Specification equalState(TicketState status) {
        return (root, query, cb) -> cb.equal(root.get("state"), status);
    }

    private static Specification betweenStartDateTimeAndEndDateTime(TicketCriteria criteria) {
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

    private static Specification betweenStartDateTime(LocalDateTime from, LocalDateTime to) {
        return (root, query, cb) -> cb.between(root.get("startDateTime"), from, to);
    }
}
