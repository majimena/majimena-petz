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
import org.majimena.petz.datatype.converters.LocalDateTimePersistenceConverter;
import org.majimena.petz.datatype.defs.Description;
import org.majimena.petz.datatype.deserializers.ISO8601LocalDateTimeDeserializer;
import org.majimena.petz.datatype.serializers.ISO8601LocalDateTimeSerializer;

import javax.persistence.Column;
import javax.persistence.Convert;
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
@Cache(usage = CacheConcurrencyStrategy.NONE)
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
    @Column(name = "subtotal", precision = 12, scale = 0, nullable = false)
    private BigDecimal subtotal;

    @NotNull
    @Column(name = "tax", precision = 9, scale = 0, nullable = false)
    private BigDecimal tax;

    @NotNull
    @Column(name = "total", precision = 12, scale = 0, nullable = false)
    private BigDecimal total;

    @Column(name = "receipt_amount", precision = 12, scale = 0, nullable = true)
    private BigDecimal receiptAmount;

    @JsonSerialize(using = ISO8601LocalDateTimeSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateTimeDeserializer.class)
    @Convert(converter = LocalDateTimePersistenceConverter.class)
    @Column(name = "receipt_date_time", nullable = true)
    private LocalDateTime receiptDateTime;

    @Size(max = Description.MAX_LENGTH)
    @Column(name = "memo", length = Description.MAX_LENGTH, nullable = true)
    private String memo;

    @Column(name = "paid", nullable = false)
    private Boolean paid;

    @Column(name = "removed", nullable = false)
    private Boolean removed;
}
