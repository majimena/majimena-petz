package org.majimena.petz.repository.spec;

import org.majimena.petz.common.utils.DateTimeUtils;
import org.majimena.petz.datatype.TicketState;
import org.majimena.petz.domain.clinic.ClinicOutlineCriteria;
import org.majimena.petz.domain.ticket.ClinicChartTicketCriteria;
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
                .and(betweenStartDateTimeAndEndDateTime(criteria.getYear(), criteria.getMonth(), criteria.getDay()));
    }

    /**
     * クリニックカルテチケットクライテリアをもとにスペックを作成する.
     *
     * @param criteria クリニックカルテチケットクライテリア
     * @return スペック
     */
    public static Specification of(ClinicChartTicketCriteria criteria) {
        return Specifications
                .where(Optional.ofNullable(criteria.getClinicId()).map(TicketSpecs::equalClinicId).orElse(null))
                .and(Optional.ofNullable(criteria.getChartId()).map(TicketSpecs::equalChartId).orElse(null))
                .and(Optional.ofNullable(criteria.getState()).map(TicketSpecs::equalState).orElse(null))
                .and(betweenStartDateTimeAndEndDateTime(criteria.getYear(), criteria.getMonth(), criteria.getDay()));
    }

    public static Specification of(ClinicOutlineCriteria criteria) {
        return Specifications
                .where(equalClinicId(criteria.getClinicId()))
                .and(Optional.ofNullable(criteria.getState()).map(TicketSpecs::equalState).orElse(null))
                .and(betweenStartDateTimeAndEndDateTime(criteria.getYear(), criteria.getMonth(), criteria.getDay()));
    }

    /**
     * 指定した期間中のチケットを検索するスペックを作成する.
     *
     * @param clinicId クリニックID
     * @param state    ステータス
     * @param from     開始日時（FROM）
     * @param to       終了日時（TO）
     * @return スペック
     */
    public static Specification of(String clinicId, TicketState state, LocalDateTime from, LocalDateTime to) {
        return Specifications
                .where(TicketSpecs.equalClinicId(clinicId))
                .and(TicketSpecs.equalState(state))
                .and(TicketSpecs.betweenStartDateTime(from, to));
    }

    public static Specification equalClinicId(String clinicId) {
        return (root, query, cb) -> {
            query.orderBy(cb.asc(root.get("startDateTime")));
            return cb.equal(root.get("clinic").get("id"), clinicId);
        };
    }

    public static Specification equalChartId(String chartId) {
        return (root, query, cb) -> cb.equal(root.get("chart").get("id"), chartId);
    }

    public static Specification equalUserId(String userId) {
        return (root, query, cb) -> cb.equal(root.get("chart").get("customer").get("user").get("id"), userId);
    }

    public static Specification equalPetId(String petId) {
        return (root, query, cb) -> cb.equal(root.get("chart").get("pet").get("id"), petId);
    }

    public static Specification equalState(TicketState status) {
        return (root, query, cb) -> cb.equal(root.get("state"), status);
    }

    private static Specification betweenStartDateTimeAndEndDateTime(Integer year, Integer month, Integer day) {
        if (year == null || month == null) {
            return null;
        }

        // 日付まで指定されていれば日付、そうでなければ範囲を月にする
        LocalDateTime from = DateTimeUtils.from(year, month, day);
        LocalDateTime to = DateTimeUtils.to(year, month, day);
        return (root, query, cb) ->
                cb.or(cb.between(root.get("startDateTime"), from, to.minusSeconds(1)), cb.between(root.get("endDateTime"), from.plusSeconds(1), to));
    }

    private static Specification betweenStartDateTime(LocalDateTime from, LocalDateTime to) {
        return (root, query, cb) -> cb.between(root.get("startDateTime"), from, to);
    }
}
