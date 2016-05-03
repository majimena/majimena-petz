package org.majimena.petical.repository.spec;

import org.majimena.petical.domain.Examination;
import org.majimena.petical.domain.ticket.ExaminationCriteria;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

import java.util.Optional;

/**
 * 診察検索スペック.
 */
public class ExaminationSpecs {

    public static Specification<Examination> of(ExaminationCriteria criteria) {
        return Specifications
                .where(Optional.ofNullable(criteria.getClinicId()).map(ExaminationSpecs::equalClinicId).orElse(null))
                .and(Optional.ofNullable(criteria.getTicketId()).map(ExaminationSpecs::equalTicketId).orElse(null));
    }

    public static Sort asc() {
        return new Sort(Sort.Direction.ASC, "examinationDateTime");
    }

    private static Specification equalClinicId(String clinicId) {
        return (root, query, cb) -> cb.equal(root.get("ticket").get("clinic").get("id"), clinicId);
    }

    private static Specification equalTicketId(String ticketId) {
        return (root, query, cb) -> cb.equal(root.get("ticket").get("id"), ticketId);
    }
}
