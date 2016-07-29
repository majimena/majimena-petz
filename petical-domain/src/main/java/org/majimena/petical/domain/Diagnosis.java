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
import org.majimena.petical.datatype.defs.Name;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 診断結果ドメイン.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "diagnosis")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Diagnosis extends AbstractAuditingEntity implements Serializable {
    /**
     * ID.
     */
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;

    /**
     * 診断区分.
     */
    @Size(max = Name.MAX_LENGTH)
    @Column(name = "type", length = Name.MAX_LENGTH, nullable = false)
    private String type;

    /**
     * 診断名称.
     */
    @Size(max = Name.MAX_LENGTH)
    @Column(name = "name", length = Name.MAX_LENGTH, nullable = false)
    private String name;

    /**
     * アニコム保険コード.
     */
    @Size(max = Name.MAX_LENGTH)
    @Column(name = "anicom_code", length = Name.MAX_LENGTH, nullable = true)
    private String anicom_code;
}
