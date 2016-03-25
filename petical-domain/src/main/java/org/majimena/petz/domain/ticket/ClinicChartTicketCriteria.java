package org.majimena.petz.domain.ticket;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.majimena.petz.datatype.TicketState;
import org.majimena.petz.datatype.deserializers.TicketStateDeserializer;
import org.majimena.petz.datatype.serializers.EnumDataTypeSerializer;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * クリニックカルテチケットクライテリア.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ClinicChartTicketCriteria implements Serializable {

    private static final long serialVersionUID = -2242306642541540913L;

    private String clinicId;

    private String chartId;

    @JsonSerialize(using = EnumDataTypeSerializer.class)
    @JsonDeserialize(using = TicketStateDeserializer.class)
    private TicketState state;

    @Min(1900)
    @Max(2999)
    private Integer year;

    @Min(1)
    @Max(12)
    private Integer month;

    @Min(1)
    @Max(31)
    private Integer day;
}
