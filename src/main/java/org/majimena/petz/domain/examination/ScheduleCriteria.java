package org.majimena.petz.domain.examination;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * スケジュールの検索条件.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ScheduleCriteria implements Serializable {

    private static final long serialVersionUID = -7025435043121115212L;

    private String clinicId;

    @NotNull
    @Min(2010)
    @Max(2999)
    private Integer year;

    @NotNull
    @Min(1)
    @Max(12)
    private Integer month;

    @Min(1)
    @Max(31)
    private Integer day;
}
