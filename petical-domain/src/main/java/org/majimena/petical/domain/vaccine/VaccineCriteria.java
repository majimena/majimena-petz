package org.majimena.petical.domain.vaccine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.majimena.petical.datatype.defs.ID;
import org.majimena.petical.datatype.defs.Name;

import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * ワクチンの検索条件.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class VaccineCriteria implements Serializable {

    @Size(max = ID.MAX_LENGTH)
    private String clinicId;

    @Size(max = Name.MAX_LENGTH)
    private String name;
}
