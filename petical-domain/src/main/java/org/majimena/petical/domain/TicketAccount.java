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
import org.majimena.petical.datatype.defs.Name;

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
 * チケット会計エンティティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
@Entity
@Table(name = "ticket_account")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TicketAccount extends AbstractAuditingEntity implements Serializable {
    /**
     * ID.
     */
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;

    /**
     * チケット.
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    /**
     * 明細名.
     */
    @NotEmpty
    @Size(max = Name.MAX_LENGTH)
    @Column(name = "name", length = Name.MAX_LENGTH, nullable = false)
    private String name;

    /**
     * 数量.
     */
    @NotNull
    @Digits(integer = 9, fraction = 0)
    @Column(name = "quantity", precision = 9, scale = 0, nullable = false)
    private BigDecimal quantity;

    /**
     * 単価.
     */
    @NotNull
    @Digits(integer = 9, fraction = 0)
    @Column(name = "price", precision = 9, scale = 0, nullable = false)
    private BigDecimal price;

    /**
     * 金額（数量＊単価）.
     */
    @NotNull
    @Digits(integer = 9, fraction = 0)
    @Column(name = "amount", precision = 9, scale = 0, nullable = false)
    private BigDecimal amount;

    /**
     * 消費税（金額＊税率）.
     */
    @NotNull
    @Digits(integer = 9, fraction = 0)
    @Column(name = "tax", precision = 9, scale = 0, nullable = false)
    private BigDecimal tax;

    /**
     * 小計.
     */
    @NotNull
    @Digits(integer = 9, fraction = 0)
    @Column(name = "subtotal", precision = 9, scale = 0, nullable = false)
    private BigDecimal subtotal;
}
