package org.majimena.petical.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.majimena.petical.datatype.PaymentType;
import org.majimena.petical.datatype.converters.LocalDateTimePersistenceConverter;
import org.majimena.petical.datatype.defs.Description;
import org.majimena.petical.datatype.deserializers.ISO8601LocalDateTimeDeserializer;
import org.majimena.petical.datatype.deserializers.PaymentTypeDeserializer;
import org.majimena.petical.datatype.serializers.EnumDataTypeSerializer;
import org.majimena.petical.datatype.serializers.ISO8601LocalDateTimeSerializer;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * チケット支払エンティティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
@Entity
@Table(name = "ticket_payment")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TicketPayment extends AbstractAuditingEntity implements Serializable {
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
     * 支払日.
     */
    @JsonSerialize(using = ISO8601LocalDateTimeSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateTimeDeserializer.class)
    @Convert(converter = LocalDateTimePersistenceConverter.class)
    @Column(name = "date", nullable = true)
    private LocalDateTime date;

    /**
     * 支払方法. // FIXME input-selectにする
     */
    @NotNull
    @JsonSerialize(using = EnumDataTypeSerializer.class)
    @JsonDeserialize(using = PaymentTypeDeserializer.class)
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private PaymentType type;

    /**
     * 請求額.
     */
    @Digits(integer = 12, fraction = 0)
    @Column(name = "total", precision = 12, scale = 0, nullable = false)
    private BigDecimal total;

    /**
     * 割引額.
     */
    @NotNull
    @Digits(integer = 12, fraction = 0)
    @Column(name = "discount", precision = 12, scale = 0, nullable = false)
    private BigDecimal discount;

    /**
     * 受領額.
     */
    @NotNull
    @Digits(integer = 12, fraction = 0)
    @Column(name = "amount", precision = 12, scale = 0, nullable = false)
    private BigDecimal amount;

    /**
     * お釣り.
     */
    @Digits(integer = 9, fraction = 0)
    @Column(name = "changed", precision = 9, scale = 0, nullable = false)
    private BigDecimal changed;

    /**
     * メモ.
     */
    @Column(name = "memo", length = Description.MAX_LENGTH)
    private String memo;
}
