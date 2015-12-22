package org.majimena.petz.domain;

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
import org.majimena.petz.datatype.InvoiceState;
import org.majimena.petz.datatype.PaymentOption;
import org.majimena.petz.datatype.converters.LocalDateTimePersistenceConverter;
import org.majimena.petz.datatype.defs.Description;
import org.majimena.petz.datatype.deserializers.ISO8601LocalDateTimeDeserializer;
import org.majimena.petz.datatype.deserializers.InvoiceStateDeserializer;
import org.majimena.petz.datatype.deserializers.PaymentOptionDeserializer;
import org.majimena.petz.datatype.serializers.EnumDataTypeSerializer;
import org.majimena.petz.datatype.serializers.ISO8601LocalDateTimeSerializer;

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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * インヴォイスドメイン.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "invoice")
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class Invoice extends AbstractAuditingEntity implements Serializable {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @NotNull
    @JsonSerialize(using = EnumDataTypeSerializer.class)
    @JsonDeserialize(using = InvoiceStateDeserializer.class)
    @Enumerated(EnumType.STRING)
    @JoinColumn(name = "state", nullable = false)
    private InvoiceState state;

    @NotNull
    @Column(name = "subtotal", precision = 12, scale = 0, nullable = false)
    private BigDecimal subtotal;

    @NotNull
    @Column(name = "tax", precision = 9, scale = 0, nullable = false)
    private BigDecimal tax;

    @NotNull
    @Column(name = "total", precision = 12, scale = 0, nullable = false)
    private BigDecimal total;

    @Column(name = "payment_amount", precision = 12, scale = 0, nullable = true)
    private BigDecimal paymentAmount;

    @JsonSerialize(using = EnumDataTypeSerializer.class)
    @JsonDeserialize(using = PaymentOptionDeserializer.class)
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_option", nullable = true)
    private PaymentOption paymentOption;

    @JsonSerialize(using = ISO8601LocalDateTimeSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateTimeDeserializer.class)
    @Convert(converter = LocalDateTimePersistenceConverter.class)
    @Column(name = "payment_date_time", nullable = true)
    private LocalDateTime paymentDateTime;

    @Size(max = Description.MAX_LENGTH)
    @Column(name = "memo", length = Description.MAX_LENGTH, nullable = true)
    private String memo;
}
