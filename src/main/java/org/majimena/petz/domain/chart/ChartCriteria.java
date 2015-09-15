package org.majimena.petz.domain.chart;

import lombok.*;
import org.majimena.petz.domain.common.defs.ID;

import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * カルテクライテリア.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ChartCriteria implements Serializable {

    private static final long serialVersionUID = -4222423315140530208L;

    @Size(max = ID.MAX_LENGTH)
    private String clinicId;

    @Size(max = ID.MAX_LENGTH)
    private String customerId;
}
