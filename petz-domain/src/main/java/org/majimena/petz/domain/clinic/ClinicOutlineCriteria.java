package org.majimena.petz.domain.clinic;

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
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * クリニックアウトラインの検索条件.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ClinicOutlineCriteria implements Serializable {

    private static final long serialVersionUID = 1391584416792059936L;

    private String clinicId;

    @JsonSerialize(using = EnumDataTypeSerializer.class)
    @JsonDeserialize(using = TicketStateDeserializer.class)
    private TicketState state;

    @NotNull
    @Min(2010)
    @Max(2999)
    private Integer year;

    @NotNull
    @Min(1)
    @Max(12)
    private Integer month;

    @NotNull
    @Min(1)
    @Max(31)
    private Integer day;
}
