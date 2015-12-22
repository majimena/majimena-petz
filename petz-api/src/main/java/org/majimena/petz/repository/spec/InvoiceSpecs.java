package org.majimena.petz.repository.spec;

import org.majimena.petz.datatype.InvoiceState;
import org.majimena.petz.domain.Invoice;
import org.majimena.petz.domain.ticket.InvoiceCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

import java.util.Optional;

/**
 * インヴォイスを検索するスペック.
 */
public class InvoiceSpecs {

    public static Specification<Invoice> of(InvoiceCriteria criteria) {
        return Specifications
                .where(Optional.ofNullable(criteria.getClinicId()).map(InvoiceSpecs::equalClinicId).orElse(null))
                .and(Optional.ofNullable(criteria.getState()).map(InvoiceSpecs::equalState).orElse(null));
    }

    private static Specification equalClinicId(String clinicId) {
        return (root, query, cb) -> cb.equal(root.get("ticket").get("clinic").get("id"), clinicId);
    }

    private static Specification equalState(InvoiceState state) {
        return (root, query, cb) -> cb.equal(root.get("state"), state.getValue());
    }
}
