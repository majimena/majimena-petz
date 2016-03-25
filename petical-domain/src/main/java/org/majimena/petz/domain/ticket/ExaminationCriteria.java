package org.majimena.petz.domain.ticket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 診察の検索条件.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ExaminationCriteria implements Serializable {

    private static final long serialVersionUID = -6541311056407101182L;

    private String clinicId;

    private String ticketId;
}
