package org.majimena.petical.repository.spec;

import org.majimena.petical.domain.TicketAttachment;
import org.majimena.petical.domain.ticket.TicketAttachmentCriteria;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

import java.util.Optional;

/**
 * チケット添付ファイルの検索スペック.
 */
public class TicketAttachmentSpecs {

    /**
     * チケット添付ファイルのクライテリアのスペックを取得する.
     *
     * @param criteria チケット添付ファイルのクライテリア
     * @return スペック
     */
    public static Specification<TicketAttachment> of(TicketAttachmentCriteria criteria) {
        return Specifications
                .where(Optional.ofNullable(criteria.getClinicId()).map(TicketAttachmentSpecs::equalClinicId).orElse(null))
                .and(Optional.ofNullable(criteria.getTicketId()).map(TicketAttachmentSpecs::equalTicketId).orElse(null));
    }

    /**
     * チケット添付ファイルのソート条件を取得する.
     *
     * @return ソート条件
     */
    public static Sort asc() {
        return new Sort(Sort.Direction.ASC, "createdDate");
    }

    /**
     * クリニックIDと完全一致するスペックを取得する.
     *
     * @param clinicId クリニックID
     * @return スペック
     */
    private static Specification equalClinicId(String clinicId) {
        return (root, query, cb) -> cb.equal(root.get("ticket").get("clinic").get("id"), clinicId);
    }

    /**
     * チケットIDと完全一致するスペックを取得する.
     *
     * @param ticketId チケットID
     * @return スペック
     */
    private static Specification equalTicketId(String ticketId) {
        return (root, query, cb) -> cb.equal(root.get("ticket").get("id"), ticketId);
    }
}
