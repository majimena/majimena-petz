package org.majimena.petical.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotEmpty;
import org.majimena.petical.datatype.defs.Description;
import org.majimena.petical.datatype.defs.Name;
import org.majimena.petical.datatype.defs.Text;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * チケット検査.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
@Entity
@Table(name = "ticket_inspection")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TicketInspection extends AbstractAuditingEntity implements Serializable {
    /**
     * ID.
     */
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;

    /**
     * チケットID.
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    /**
     * 動物病院向け検査料金ID.
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinic_inspection_id", nullable = false)
    private ClinicInspection clinicInspection;

    /**
     * 検査名.
     */
    @Size(max = Name.MAX_LENGTH)
    @Column(name = "name", length = Name.MAX_LENGTH, nullable = false)
    private String name;

    /**
     * 単価.
     */
    @Digits(integer = 9, fraction = 0)
    @Column(name = "price", precision = 9, scale = 0, nullable = false)
    private BigDecimal price;

    /**
     * 数量.
     */
    @NotNull
    @Digits(integer = 6, fraction = 0)
    @Column(name = "quantity", precision = 6, scale = 0, nullable = false)
    private BigDecimal quantity;

    /**
     * 金額（単価＊数量）.
     */
    @Digits(integer = 9, fraction = 0)
    @Column(name = "amount", precision = 9, scale = 0, nullable = false)
    private BigDecimal amount;

    /**
     * 税額（金額＊税率）.
     */
    @Digits(integer = 9, fraction = 0)
    @Column(name = "tax", precision = 9, scale = 0, nullable = false)
    private BigDecimal tax;

    /**
     * 小計（金額＋税額）.
     */
    @Digits(integer = 9, fraction = 0)
    @Column(name = "subtotal", precision = 9, scale = 0, nullable = false)
    private BigDecimal subtotal;

    /**
     * メモ.
     */
    @Size(max = Description.MAX_LENGTH)
    @Column(name = "memo", length = Description.MAX_LENGTH, nullable = true)
    private String memo;

    /**
     * 元となる検査マスタの内容（JSON形式）.
     */
    @Size(max = Text.MAX_LENGTH)
    @Column(name = "original", length = Text.MAX_LENGTH, nullable = false)
    private String original;
}
