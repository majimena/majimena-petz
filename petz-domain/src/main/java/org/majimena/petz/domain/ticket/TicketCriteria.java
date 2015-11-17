package org.majimena.petz.domain.ticket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.majimena.petz.datatype.TicketState;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * チケットの検索条件.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TicketCriteria implements Serializable {

    private static final long serialVersionUID = -7025435043121115212L;

    private String clinicId;

    private String userId;

    private String petId;

    private TicketState status;

    @Min(2010)
    @Max(2999)
    private Integer year;

    @Min(1)
    @Max(12)
    private Integer month;

    @Min(1)
    @Max(31)
    private Integer day;
}
