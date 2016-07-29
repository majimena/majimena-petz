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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 動物用医薬品アイテム成分.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "nval_item_component")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class NvalItemComponent extends AbstractAuditingEntity implements Serializable {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nval_item_id", nullable = false)
    private NvalItem nvalItem;

    @Size(max = Description.MAX_LENGTH)
    @Column(name = "name", length = Description.MAX_LENGTH, nullable = true)
    private String name;

    @Size(max = Description.MAX_LENGTH)
    @Column(name = "category_name", length = Description.MAX_LENGTH, nullable = true)
    private String categoryName;
}
