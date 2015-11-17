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
import org.majimena.petz.datatype.TaxType;
import org.majimena.petz.datatype.converters.LocalDateTimePersistenceConverter;
import org.majimena.petz.datatype.defs.Name;
import org.majimena.petz.datatype.deserializers.ISO8601LocalDateTimeDeserializer;
import org.majimena.petz.datatype.deserializers.TaxTypeDeserializer;
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
 * 診察ドメイン.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "examination")
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class Examination extends AbstractAuditingEntity implements Serializable {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull
    @Size(max = Name.MAX_LENGTH)
    @Column(name = "name", length = Name.MAX_LENGTH, nullable = false)
    private String name;

    @NotNull
    @Column(name = "price", precision = 9, scale = 0, nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @JsonSerialize(using = EnumDataTypeSerializer.class)
    @JsonDeserialize(using = TaxTypeDeserializer.class)
    @Column(name = "tax_type", length = 20, nullable = false)
    private TaxType taxType;

    @NotNull
    @Column(name = "tax_rate", precision = 3, scale = 2, nullable = false)
    private BigDecimal taxRate;

    @NotNull
    @Column(name = "tax", precision = 9, scale = 0, nullable = false)
    private BigDecimal tax;

    @NotNull
    @Column(name = "quantity", precision = 3, scale = 0, nullable = false)
    private BigDecimal quantity;

    @Column(name = "total", precision = 12, scale = 0, nullable = false)
    private BigDecimal total;

    @Size(max = 10000)
    @Column(name = "memo", length = 10000, nullable = true)
    private String memo;

    @JsonSerialize(using = ISO8601LocalDateTimeSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateTimeDeserializer.class)
    @Convert(converter = LocalDateTimePersistenceConverter.class)
    @Column(name = "examination_date_time", nullable = false)
    private LocalDateTime examinationDateTime;

    @Column(name = "removed", nullable = false)
    private Boolean removed;
}
