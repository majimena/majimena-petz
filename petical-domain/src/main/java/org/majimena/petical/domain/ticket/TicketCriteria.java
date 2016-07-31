package org.majimena.petical.domain.ticket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.majimena.petical.datatype.TicketState;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

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

    @Deprecated
    private String userId;

    @Deprecated
    private String petId;

    private TicketState state;

    /**
     * 対象外ステート一覧.
     */
    private List<TicketState> not;

    /**
     * 対象日（指定日以前）.
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate to;

    @Deprecated
    @Min(2010)
    @Max(2999)
    private Integer year;

    @Deprecated
    @Min(1)
    @Max(12)
    private Integer month;

    @Deprecated
    @Min(1)
    @Max(31)
    private Integer day;
}
