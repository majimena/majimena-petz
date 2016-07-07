package org.majimena.petical.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.majimena.petical.datatype.defs.Description;
import org.majimena.petical.datatype.defs.Text;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 動物用医薬品ドメイン.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "medicine")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Medicine extends AbstractAuditingEntity implements Serializable {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;

    @Size(max = Description.MAX_LENGTH)
    @Column(name = "nval_id", length = Description.MAX_LENGTH, nullable = true)
    private String nvalId;

    @Size(max = Description.MAX_LENGTH)
    @Column(name = "name", length = Description.MAX_LENGTH, nullable = true)
    private String name;

    @Size(max = Description.MAX_LENGTH)
    @Column(name = "dealer_name", length = Description.MAX_LENGTH, nullable = true)
    private String dealerName;

    @Size(max = Description.MAX_LENGTH)
    @Column(name = "preparation_type", length = Description.MAX_LENGTH, nullable = true)
    private String preparationType;

    @Size(max = Description.MAX_LENGTH)
    @Column(name = "form_type", length = Description.MAX_LENGTH, nullable = true)
    private String formType;

    @Size(max = Description.MAX_LENGTH)
    @Column(name = "regulation_type", length = Description.MAX_LENGTH, nullable = true)
    private String regulationType;

    @Size(max = Text.MAX_LENGTH)
    @Column(name = "effect", nullable = true)
    private String effect;

    @Column(name = "side_effect", nullable = true)
    private Boolean sideEffect;

    @Column(name = "official", nullable = false)
    private Boolean official;
}
